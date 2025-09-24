package org.example.remedy.application.song;

import lombok.RequiredArgsConstructor;
import org.example.remedy.application.song.port.out.SongPersistencePort;
import org.example.remedy.domain.song.Song;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
public class SongBatchProcessingService {

    private static final Logger logger = LoggerFactory.getLogger(SongBatchProcessingService.class);

    private final YouTubeDownloadService youTubeDownloadService;
    private final SpotifyImageService spotifyImageService;
    private final HLSService hlsService;
    private final SongPersistencePort songPersistencePort;

    private final ExecutorService executorService = Executors.newFixedThreadPool(5);

    public List<SongProcessingResult> processSongBatch(List<String> songTitles) {
        logger.info("노래 일괄 처리 시작: {} 곡", songTitles.size());

        List<CompletableFuture<SongProcessingResult>> futures = new ArrayList<>();

        for (String songTitle : songTitles) {
            CompletableFuture<SongProcessingResult> future = CompletableFuture
                    .supplyAsync(() -> processSingleSong(songTitle), executorService);
            futures.add(future);
        }

        List<SongProcessingResult> results = new ArrayList<>();
        for (CompletableFuture<SongProcessingResult> future : futures) {
            try {
                results.add(future.get());
            } catch (Exception e) {
                logger.error("노래 처리 중 오류 발생", e);
                results.add(SongProcessingResult.failure("알 수 없는 오류", e.getMessage()));
            }
        }

        logger.info("노래 일괄 처리 완료: {} 곡 처리", results.size());
        return results;
    }

    private SongProcessingResult processSingleSong(String songTitle) {
        logger.info("노래 처리 시작: {}", songTitle);

        try {
            // 1. Spotify에서 정확한 트랙 정보 검색 (제목, 아티스트, 앨범 이미지)
            SpotifyImageService.SpotifyAlbumImageResult spotifyResult =
                spotifyImageService.searchAndDownloadAlbumImage(songTitle, "");

            // Spotify에서 찾은 정보를 우선 사용, 없으면 원본 제목 사용
            String finalTitle = spotifyResult.isFound() ? spotifyResult.getTrackName() : songTitle;
            String finalArtist = spotifyResult.isFound() ? spotifyResult.getArtistName() : "";

            // 2. YouTube에서 음원 검색 및 다운로드 (MP3만 가져옴)
            YouTubeDownloadService.YouTubeSearchResult youtubeResult =
                youTubeDownloadService.searchAndDownload(finalTitle + " " + finalArtist);

            // 3. MP3를 HLS로 변환
            String songId = UUID.randomUUID().toString();
            String hlsPath = hlsService.convertToHLS(youtubeResult.getDownloadPath(), songId);

            // 4. Song 엔티티 생성 및 저장 (Spotify 정보 우선 사용)
            Song song = Song.builder()
                    .id(songId)
                    .title(finalTitle)  // Spotify에서 가져온 정확한 제목
                    .artist(finalArtist)  // Spotify에서 가져온 정확한 아티스트
                    .duration(youtubeResult.getDuration())
                    .hlsPath(hlsPath)
                    .albumImagePath(spotifyResult.isFound() ? spotifyResult.getLocalPath() : null)
                    .build();

            Song savedSong = songPersistencePort.save(song);

            logger.info("노래 처리 완료: {} by {} (ID: {})", savedSong.getTitle(), savedSong.getArtist(), savedSong.getId());

            return SongProcessingResult.success(
                savedSong.getId(),
                savedSong.getTitle(),
                savedSong.getArtist(),
                savedSong.getHlsPath(),
                savedSong.getAlbumImagePath()
            );

        } catch (Exception e) {
            logger.error("노래 처리 실패: {}", songTitle, e);
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
}