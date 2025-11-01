package org.example.remedy.application.song;

import lombok.extern.slf4j.Slf4j;
import org.example.remedy.application.song.dto.SongProcessingResult;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

/**
 * 로컬 파일 정리 책임
 * - 처리 완료된 MP3 파일 삭제
 * - HLS 디렉토리는 HLSService에서 이미 삭제됨
 */
@Slf4j
@Component
public class LocalFileCleanupService {

    public void cleanupAll(List<SongProcessingResult> results) {
        log.info("--- 로컬 파일 정리 시작 ---");

        for (SongProcessingResult result : results) {
            cleanupMp3File(result);
        }
    }

    private void cleanupMp3File(SongProcessingResult result) {
        if (result.getMp3LocalPath() == null) {
            return;
        }

        try {
            File mp3File = new File(result.getMp3LocalPath());
            if (mp3File.exists() && mp3File.delete()) {
                log.debug("로컬 MP3 파일 삭제: {}", result.getMp3LocalPath());
            }
        } catch (Exception e) {
            log.warn("로컬 MP3 파일 삭제 실패: {}", result.getMp3LocalPath(), e);
        }
    }
}
