package org.example.remedy.application.song;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.remedy.application.song.dto.response.SongDownloadResponse;
import org.example.remedy.application.song.port.out.SongPersistencePort;
import org.example.remedy.domain.song.Song;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SongBatchProcessingService {

    private final YouTubeDownloadService youTubeDownloadService;
    private final SpotifyImageService spotifyImageService;
    private final HLSService hlsService;
    private final SongPersistencePort songPersistencePort;

    private final ExecutorService ioExecutor = Executors.newCachedThreadPool();
    private final ForkJoinPool cpuPool = ForkJoinPool.commonPool();

    private static final long BATCH_TIMEOUT_MS = 600_000; // 10분
    private static final long SONG_TIMEOUT_MS = 300_000; // 5분

    public List<SongDownloadResponse> processSongBatch(List<String> songTitles) {
        log.info("노래 일괄 처리 시작: {}곡", songTitles.size());
        long start = System.currentTimeMillis();

        try {
            spotifyImageService.ensureValidToken();
        } catch (Exception e) {
            log.warn("Spotify 토큰 확보 실패: {}", e.getMessage());
        }

        List<CompletableFuture<SongDownloadResponse>> futures = songTitles.stream()
                .map(title -> CompletableFuture.supplyAsync(() -> processSingleSong(title), ioExecutor)
                        .exceptionally(ex -> SongDownloadResponse.failure(title, ex.getMessage())))
                .toList();

        CompletableFuture<Void> all = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        List<SongDownloadResponse> results;
        try {
            // 전체 배치 타임아웃 적용 (10분)
            all.get(BATCH_TIMEOUT_MS, TimeUnit.MILLISECONDS);

            // 완료된 결과 수집 (이미 완료됨)
            results = futures.stream()
                    .map(CompletableFuture::join)
                    .toList();

        } catch (Exception e) {
            log.error("배치 처리 중 예외 또는 타임아웃 발생", e);

            // 완료된 작업만 수집, 나머지는 취소
            results = futures.stream()
                    .map(f -> f.isDone() ? f.join() :
                            SongDownloadResponse.failure("타임아웃", "작업이 시간 내에 완료되지 않음"))
                    .toList();
        }

        log.info("노래 일괄 처리 완료: {}곡, 소요시간: {}ms",
                results.size(), System.currentTimeMillis() - start);
        return results;
    }

    private SongDownloadResponse processSingleSong(String songTitle) {
        long songStart = System.currentTimeMillis();

        try {
            String songId = UUID.randomUUID().toString();

            // I/O 작업 병렬
            CompletableFuture<SpotifyImageService.SpotifyAlbumImageResult> spotifyFuture =
                    CompletableFuture.supplyAsync(() -> spotifyImageService.searchAndDownloadAlbumImage(songTitle, ""), ioExecutor)
                            .exceptionally(ex -> SpotifyImageService.SpotifyAlbumImageResult.builder().found(false).build());

            CompletableFuture<YouTubeDownloadService.YouTubeSearchResult> youtubeFuture =
                    CompletableFuture.supplyAsync(() -> youTubeDownloadService.searchAndDownload(songTitle + " audio"), ioExecutor);

            // 병합 후 결과 가져오기 (5분 타임아웃)
            CompletableFuture.allOf(spotifyFuture, youtubeFuture)
                    .get(SONG_TIMEOUT_MS, TimeUnit.MILLISECONDS);

            SpotifyImageService.SpotifyAlbumImageResult spotify = spotifyFuture.join();
            YouTubeDownloadService.YouTubeSearchResult youtube = youtubeFuture.join();

            String finalTitle = spotify.isFound() ? spotify.getTrackName() : songTitle;
            String finalArtist = spotify.isFound() ? spotify.getArtistName() : "";

            // CPU 작업
            String hlsPath = CompletableFuture.supplyAsync(() ->
                    {
                        try {
                            return hlsService.convertToHLS(youtube.getDownloadPath(), songId);
                        } catch (IOException | InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }, cpuPool)
                    .get(SONG_TIMEOUT_MS, TimeUnit.MILLISECONDS);

            Song song = Song.builder()
                    .id(songId)
                    .title(finalTitle)
                    .artist(finalArtist)
                    .duration(youtube.getDuration())
                    .hlsPath(hlsPath)
                    .albumImagePath(spotify.isFound() ? spotify.getLocalPath() : null)
                    .build();

            Song saved = songPersistencePort.save(song);

            log.info("노래 처리 완료: {} by {} (ID: {}), 소요시간: {}ms",
                    saved.getTitle(), saved.getArtist(), saved.getId(), System.currentTimeMillis() - songStart);

            return SongDownloadResponse.success(saved.getId(), saved.getTitle(),
                    saved.getArtist(), saved.getHlsPath(), saved.getAlbumImagePath());

        } catch (Exception e) {
            log.error("노래 처리 실패: {}, 소요시간: {}ms", songTitle, System.currentTimeMillis() - songStart, e);
            return SongDownloadResponse.failure(songTitle, e.getMessage());
        }
    }

    @PreDestroy
    public void cleanup() {
        ioExecutor.shutdown();
        try {
            if (!ioExecutor.awaitTermination(10, TimeUnit.SECONDS)) ioExecutor.shutdownNow();
        } catch (InterruptedException e) {
            ioExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}