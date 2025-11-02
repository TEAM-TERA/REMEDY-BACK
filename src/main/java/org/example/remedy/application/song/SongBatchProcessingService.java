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
    private final HLSBatchConverter hlsConverter;
    private final S3BatchUploader s3Uploader;
    private final SongPersistencePort songPersistencePort;
    private final LocalFileCleanupService cleanupService;

    public List<SongDownloadResponse> processSongBatch(SongBatchDownloadRequest request) {
        long batchStart = System.currentTimeMillis();
        int totalCount = request.songDownloadRequests().size();
        log.info("=== 노래 배치 처리 시작: {}곡 ===", totalCount);

        ensureSpotifyToken();
        List<SongProcessingResult> results = downloadAllSongs(request.songDownloadRequests());

        hlsConverter.convertAll(results);
        s3Uploader.uploadAll(results);
        List<SongDownloadResponse> responses = saveAllToDatabase(results);
        cleanupService.cleanupAll(results);

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

    private List<SongDownloadResponse> saveAllToDatabase(List<SongProcessingResult> results) {
        log.info("--- 4단계: DB 저장 시작 ---");

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