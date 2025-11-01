package org.example.remedy.application.song;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.remedy.application.song.dto.SongProcessingResult;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

/**
 * S3 배치 업로드 책임
 * - MP3 파일과 HLS 파일들을 S3에 일괄 업로드
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class S3BatchUploader {

    private final YouTubeDownloadService youTubeDownloadService;
    private final HLSService hlsService;

    public void uploadAll(List<SongProcessingResult> results) {
        log.info("--- S3 일괄 업로드 시작 ---");

        for (SongProcessingResult result : results) {
            if (!result.isSuccess()) {
                continue;
            }

            uploadSingle(result);
        }
    }

    private void uploadSingle(SongProcessingResult result) {
        try {
            uploadMp3(result);
            uploadHls(result);
            log.info("S3 업로드 완료: {} (ID: {})", result.getTitle(), result.getSongId());
        } catch (Exception e) {
            log.error("S3 업로드 실패: {} by {}", result.getTitle(), result.getArtist(), e);
            markAsFailed(result, "S3 업로드 실패: " + e.getMessage());
        }
    }

    private void uploadMp3(SongProcessingResult result) throws Exception {
        String mp3S3Url = youTubeDownloadService.uploadToS3(
                result.getMp3LocalPath(),
                result.getSongId()
        );
        result.setMp3S3Url(mp3S3Url);
    }

    private void uploadHls(SongProcessingResult result) throws Exception {
        String hlsS3Url = hlsService.uploadHLSFilesToS3(
                new File(result.getHlsLocalDir()),
                result.getSongId()
        );
        result.setHlsS3Url(hlsS3Url);
    }

    private void markAsFailed(SongProcessingResult result, String errorMessage) {
        result.setSuccess(false);
        result.setErrorMessage(errorMessage);
    }
}
