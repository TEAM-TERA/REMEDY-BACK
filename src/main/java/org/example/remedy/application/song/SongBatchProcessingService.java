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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
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

        // 타임아웃을 설정하여 무한 대기 방지 (최대 10분)
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0])
        );

        List<SongProcessingResult> results = new ArrayList<>();
        try {
            // 진행 상황을 주기적으로 체크하면서 대기
            long waitStart = System.currentTimeMillis();
            while (!allFutures.isDone()) {
                try {
                    allFutures.get(30, TimeUnit.SECONDS); // 30초마다 체크
                    break; // 완료되면 루프 종료
                } catch (TimeoutException te) {
                    // 진행 상황 로깅
                    long completedCount = futures.stream()
                            .mapToLong(f -> f.isDone() ? 1 : 0)
                            .sum();
                    long elapsed = System.currentTimeMillis() - waitStart;
                    logger.info("배치 처리 진행 상황: {}/{} 곡 완료, 경과시간: {}ms",
                            completedCount, futures.size(), elapsed);

                    // 전체 10분 타임아웃 체크
                    if (elapsed > 600_000) { // 10분
                        throw new TimeoutException("전체 배치 처리 10분 타임아웃");
                    }
                }
            }

            // 모든 작업 완료 후 결과 수집
            results = futures.stream()
                    .map(future -> {
                        try {
                            return future.get(1, TimeUnit.SECONDS); // 이미 완료된 상태이므로 짧은 타임아웃
                        } catch (Exception e) {
                            logger.error("완료된 작업 결과 수집 실패", e);
                            return SongProcessingResult.failure("알 수 없음", e.getMessage());
                        }
                    })
                    .collect(Collectors.toList());

        } catch (TimeoutException e) {
            logger.error("배치 처리 타임아웃 발생 - 부분 결과 반환");
            // 타임아웃 발생 시 완료된 작업들의 결과만 수집
            results = futures.stream()
                    .map(future -> {
                        if (future.isDone()) {
                            try {
                                return future.get(100, TimeUnit.MILLISECONDS);
                            } catch (Exception ex) {
                                logger.warn("완료된 작업 결과 가져오기 실패", ex);
                                return SongProcessingResult.failure("타임아웃", "작업 타임아웃");
                            }
                        } else {
                            future.cancel(true); // 미완료 작업 취소
                            return SongProcessingResult.failure("타임아웃", "작업이 시간 내에 완료되지 않음");
                        }
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("배치 처리 중 예외 발생", e);
            // 예외 발생 시에도 가능한 결과들 수집
            results = futures.stream()
                    .map(future -> {
                        if (future.isDone() && !future.isCancelled()) {
                            try {
                                return future.get(100, TimeUnit.MILLISECONDS);
                            } catch (Exception ex) {
                                return SongProcessingResult.failure("오류", ex.getMessage());
                            }
                        } else {
                            return SongProcessingResult.failure("처리 실패", "배치 처리 중 오류 발생");
                        }
                    })
                    .collect(Collectors.toList());
        }

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

            // 두 작업이 모두 완료되면 결합 (타임아웃 5분)
            CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(spotifyFuture, youtubeFuture);

            // 타임아웃을 설정하여 개별 작업도 무한 대기 방지
            combinedFuture.get(5, TimeUnit.MINUTES);

            SpotifyImageService.SpotifyAlbumImageResult spotifyResult = spotifyFuture.get(10, TimeUnit.SECONDS);
            YouTubeDownloadService.YouTubeSearchResult youtubeResult = youtubeFuture.get(10, TimeUnit.SECONDS);

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

            String hlsPath = hlsFuture.get(5, TimeUnit.MINUTES); // HLS 변환도 타임아웃 설정

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