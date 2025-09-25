package org.example.remedy.application.song;

import lombok.RequiredArgsConstructor;
import org.example.remedy.application.song.port.out.SongPersistencePort;
import org.example.remedy.domain.song.Song;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import jakarta.annotation.PreDestroy;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SongBatchProcessingService {

    private static final Logger logger = LoggerFactory.getLogger(SongBatchProcessingService.class);

    private final YouTubeDownloadService youTubeDownloadService;
    private final SpotifyImageService spotifyImageService;
    private final HLSService hlsService;
    private final SongPersistencePort songPersistencePort;

    // 동적 스레드풀 사용 - CPU 코어 수에 따라 조정
    private final ExecutorService ioExecutorService = Executors.newCachedThreadPool();
    private final ForkJoinPool cpuBoundPool = ForkJoinPool.commonPool();

    public List<SongProcessingResult> processSongBatch(List<String> songTitles) {
        logger.info("노래 일괄 처리 시작: {} 곡", songTitles.size());
        long startTime = System.currentTimeMillis();

        // Spotify 토큰을 미리 확보 (배치 처리 시작 시 한 번만)
        try {
            spotifyImageService.ensureValidToken();
        } catch (Exception e) {
            logger.warn("Spotify 토큰 확보 실패, 개별 처리로 진행: {}", e.getMessage());
        }

        // CompletableFuture.allOf()를 사용한 병렬 처리 최적화
        List<CompletableFuture<SongProcessingResult>> futures = songTitles.stream()
                .map(songTitle -> CompletableFuture
                        .supplyAsync(() -> processSingleSong(songTitle), ioExecutorService)
                        .exceptionally(throwable -> {
                            logger.error("노래 처리 중 예외 발생: {}", songTitle, throwable);
                            return SongProcessingResult.failure(songTitle, throwable.getMessage());
                        }))
                .toList();

        // 모든 작업이 완료될 때까지 대기 (non-blocking)
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0])
        );

        // 결과 수집 (모든 작업 완료 후)
        List<SongProcessingResult> results = allFutures
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList()))
                .join();

        long endTime = System.currentTimeMillis();
        logger.info("노래 일괄 처리 완료: {} 곡 처리, 소요시간: {}ms",
                results.size(), endTime - startTime);

        return results;
    }

    private SongProcessingResult processSingleSong(String songTitle) {
        logger.info("노래 처리 시작: {}", songTitle);
        long songStartTime = System.currentTimeMillis();

        try {
            String songId = UUID.randomUUID().toString();

            // 1, 2단계를 병렬로 처리: Spotify 정보 검색과 YouTube 검색을 동시에 실행
            CompletableFuture<SpotifyImageService.SpotifyAlbumImageResult> spotifyFuture =
                    CompletableFuture.supplyAsync(() -> {
                        try {
                            return spotifyImageService.searchAndDownloadAlbumImage(songTitle, "");
                        } catch (Exception e) {
                            logger.warn("Spotify 검색 실패: {}, 기본값 사용", songTitle, e);
                            return SpotifyImageService.SpotifyAlbumImageResult.builder()
                                    .found(false)
                                    .build();
                        }
                    }, ioExecutorService);

            CompletableFuture<YouTubeDownloadService.YouTubeSearchResult> youtubeFuture =
                    CompletableFuture.supplyAsync(() ->
                            youTubeDownloadService.searchAndDownload(songTitle + " audio"), ioExecutorService);

            // 두 작업이 모두 완료되면 결합
            CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(spotifyFuture, youtubeFuture);

            // 결과 대기 및 처리
            combinedFuture.join();

            SpotifyImageService.SpotifyAlbumImageResult spotifyResult = spotifyFuture.join();
            YouTubeDownloadService.YouTubeSearchResult youtubeResult = youtubeFuture.join();

            // Spotify에서 찾은 정보를 우선 사용, 없으면 원본 제목 사용
            String finalTitle = spotifyResult.isFound() ? spotifyResult.getTrackName() : songTitle;
            String finalArtist = spotifyResult.isFound() ? spotifyResult.getArtistName() : "";

            // 3. MP3를 HLS로 변환 (CPU 집약적 작업이므로 별도 스레드풀 사용)
            CompletableFuture<String> hlsFuture = CompletableFuture.supplyAsync(() -> {
                try {
                    return hlsService.convertToHLS(youtubeResult.getDownloadPath(), songId);
                } catch (Exception e) {
                    throw new RuntimeException("HLS 변환 실패", e);
                }
            }, cpuBoundPool);

            String hlsPath = hlsFuture.join();

            // 4. Song 엔티티 생성 및 저장
            Song song = Song.builder()
                    .id(songId)
                    .title(finalTitle)
                    .artist(finalArtist)
                    .duration(youtubeResult.getDuration())
                    .hlsPath(hlsPath)
                    .albumImagePath(spotifyResult.isFound() ? spotifyResult.getLocalPath() : null)
                    .build();

            Song savedSong = songPersistencePort.save(song);

            long songEndTime = System.currentTimeMillis();
            logger.info("노래 처리 완료: {} by {} (ID: {}), 소요시간: {}ms",
                    savedSong.getTitle(), savedSong.getArtist(), savedSong.getId(),
                    songEndTime - songStartTime);

            return SongProcessingResult.success(
                savedSong.getId(),
                savedSong.getTitle(),
                savedSong.getArtist(),
                savedSong.getHlsPath(),
                savedSong.getAlbumImagePath()
            );

        } catch (Exception e) {
            long songEndTime = System.currentTimeMillis();
            logger.error("노래 처리 실패: {}, 소요시간: {}ms", songTitle,
                    songEndTime - songStartTime, e);
            return SongProcessingResult.failure(songTitle, e.getMessage());
        }
    }

    public static class SongProcessingResult {
        private boolean success;
        private String songId;
        private String title;
        private String artist;
        private String hlsPath;
        private String albumImagePath;
        private String errorMessage;

        public static SongProcessingResult success(String songId, String title, String artist, String hlsPath, String albumImagePath) {
            SongProcessingResult result = new SongProcessingResult();
            result.success = true;
            result.songId = songId;
            result.title = title;
            result.artist = artist;
            result.hlsPath = hlsPath;
            result.albumImagePath = albumImagePath;
            return result;
        }

        public static SongProcessingResult failure(String title, String errorMessage) {
            SongProcessingResult result = new SongProcessingResult();
            result.success = false;
            result.title = title;
            result.errorMessage = errorMessage;
            return result;
        }

        // Getters
        public boolean isSuccess() { return success; }
        public String getSongId() { return songId; }
        public String getTitle() { return title; }
        public String getArtist() { return artist; }
        public String getHlsPath() { return hlsPath; }
        public String getAlbumImagePath() { return albumImagePath; }
        public String getErrorMessage() { return errorMessage; }
    }

    @PreDestroy
    public void cleanup() {
        logger.info("SongBatchProcessingService 정리 중...");
        ioExecutorService.shutdown();
        try {
            if (!ioExecutorService.awaitTermination(10, java.util.concurrent.TimeUnit.SECONDS)) {
                ioExecutorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            ioExecutorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
        logger.info("SongBatchProcessingService 정리 완료");
    }
}