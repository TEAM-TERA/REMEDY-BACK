package org.example.remedy.application.song;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.remedy.application.song.dto.SongProcessingResult;
import org.example.remedy.application.song.dto.response.SongDownloadResponse;
import org.example.remedy.application.song.port.out.SongPersistencePort;
import org.example.remedy.domain.song.Song;
import org.example.remedy.presentation.song.dto.request.SongBatchDownloadRequest;
import org.example.remedy.presentation.song.dto.request.SongDownloadRequest;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 곡 배치 처리 파사드 서비스
 * - 각 단계별 서비스를 조합하여 배치 처리 흐름 제어
 * - SOLID 원칙 준수: 단일 책임 원칙, 의존성 역전 원칙
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SongBatchProcessingService {

    private final SpotifyImageService spotifyImageService;
    private final SongDownloadOrchestrator downloadOrchestrator;
    private final HLSService hlsService;
    private final YouTubeDownloadService youTubeDownloadService;
    private final SongPersistencePort songPersistencePort;

    public List<SongDownloadResponse> processSongBatch(SongBatchDownloadRequest request) {
        long batchStart = System.currentTimeMillis();
        int totalCount = request.songDownloadRequests().size();
        log.info("=== 노래 배치 처리 시작: {}곡 ===", totalCount);

        ensureSpotifyToken();
        List<SongProcessingResult> results = downloadAllSongs(request.songDownloadRequests());

        processAllSongs(results);
        List<SongDownloadResponse> responses = saveAllToDatabase(results);

        logBatchCompletion(totalCount, batchStart);
        return responses;
    }

    private void ensureSpotifyToken() {
        try {
            spotifyImageService.ensureValidToken();
        } catch (Exception e) {
            log.warn("Spotify 토큰 확보 실패: {}", e.getMessage());
        }
    }

    private List<SongProcessingResult> downloadAllSongs(List<SongDownloadRequest> requests) {
        log.info("--- 1단계: 메타데이터 조회 및 YouTube 다운로드 시작 ---");

        List<SongProcessingResult> results = new ArrayList<>();
        for (SongDownloadRequest request : requests) {
            SongProcessingResult result = downloadOrchestrator.downloadSong(request);
            results.add(result);
        }

        return results;
    }

    private void processAllSongs(List<SongProcessingResult> results) {
        log.info("--- 2단계: HLS 변환 및 S3 업로드 시작 ---");

        for (SongProcessingResult result : results) {
            if (!result.isSuccess()) {
                continue;
            }

            processSingleSong(result);
        }
    }

    private void processSingleSong(SongProcessingResult result) {
        try {
            // HLS 변환
            String hlsDir = hlsService.convertToHLSLocal(
                    result.getMp3LocalPath(),
                    result.getSongId()
            );
            log.info("HLS 변환 완료: {} (ID: {})", result.getTitle(), result.getSongId());

            // MP3 S3 업로드
            String mp3S3Url = youTubeDownloadService.uploadToS3(
                    result.getMp3LocalPath(),
                    result.getSongId()
            );
            result.setMp3S3Url(mp3S3Url);
            log.info("MP3 S3 업로드 완료: {}", mp3S3Url);

            // HLS S3 업로드
            String hlsS3Url = hlsService.uploadHLSFilesToS3(
                    new File(hlsDir),
                    result.getSongId()
            );
            result.setHlsS3Url(hlsS3Url);
            log.info("HLS S3 업로드 완료: {}", hlsS3Url);

            // 로컬 MP3 파일 정리
            cleanupLocalMp3(result.getMp3LocalPath());

        } catch (Exception e) {
            log.error("곡 처리 실패: {} by {}", result.getTitle(), result.getArtist(), e);
            result.setSuccess(false);
            result.setErrorMessage("곡 처리 실패: " + e.getMessage());
        }
    }

    private void cleanupLocalMp3(String mp3LocalPath) {
        if (mp3LocalPath == null) {
            return;
        }

        try {
            File mp3File = new File(mp3LocalPath);
            if (mp3File.exists() && mp3File.delete()) {
                log.debug("로컬 MP3 파일 삭제: {}", mp3LocalPath);
            }
        } catch (Exception e) {
            log.warn("로컬 MP3 파일 삭제 실패: {}", mp3LocalPath, e);
        }
    }

    private List<SongDownloadResponse> saveAllToDatabase(List<SongProcessingResult> results) {
        log.info("--- 3단계: DB 저장 시작 ---");

        List<SongDownloadResponse> responses = new ArrayList<>();
        for (SongProcessingResult result : results) {
            SongDownloadResponse response = saveSingleSong(result);
            responses.add(response);
        }

        return responses;
    }

    private SongDownloadResponse saveSingleSong(SongProcessingResult result) {
        if (!result.isSuccess()) {
            return createFailureResponse(result);
        }

        try {
            Song song = buildSongEntity(result);
            Song saved = songPersistencePort.save(song);

            log.info("DB 저장 완료: {} by {} (ID: {})",
                    saved.getTitle(), saved.getArtist(), saved.getId());

            return createSuccessResponse(saved);
        } catch (Exception e) {
            log.error("DB 저장 실패: {} by {}", result.getTitle(), result.getArtist(), e);
            return SongDownloadResponse.failure(
                    result.getTitle(),
                    result.getArtist(),
                    "DB 저장 실패: " + e.getMessage()
            );
        }
    }

    private Song buildSongEntity(SongProcessingResult result) {
        return Song.builder()
                .id(result.getSongId())
                .title(result.getTitle())
                .artist(result.getArtist())
                .duration(result.getDuration())
                .hlsPath(result.getHlsS3Url())
                .mp3Path(result.getMp3S3Url())
                .albumImagePath(result.getAlbumImageS3Url())
                .build();
    }

    private SongDownloadResponse createSuccessResponse(Song song) {
        return SongDownloadResponse.success(
                song.getId(),
                song.getTitle(),
                song.getArtist(),
                song.getHlsPath(),
                song.getAlbumImagePath()
        );
    }

    private SongDownloadResponse createFailureResponse(SongProcessingResult result) {
        return SongDownloadResponse.failure(
                result.getTitle(),
                result.getArtist(),
                result.getErrorMessage()
        );
    }

    private void logBatchCompletion(int totalCount, long batchStart) {
        long totalTime = System.currentTimeMillis() - batchStart;
        log.info("=== 배치 처리 완료: 총 {}곡, 소요시간: {}ms ===", totalCount, totalTime);
    }
}