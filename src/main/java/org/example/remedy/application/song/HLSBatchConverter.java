package org.example.remedy.application.song;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.remedy.application.song.dto.SongProcessingResult;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * HLS 배치 변환 책임
 * - 여러 곡의 HLS 변환을 순차 처리
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HLSBatchConverter {

    private final HLSService hlsService;

    public void convertAll(List<SongProcessingResult> results) {
        log.info("--- HLS 변환 시작 ---");

        for (SongProcessingResult result : results) {
            if (!result.isSuccess()) {
                continue;
            }

            convertSingle(result);
        }
    }

    private void convertSingle(SongProcessingResult result) {
        try {
            String hlsDir = hlsService.convertToHLSLocal(
                    result.getMp3LocalPath(),
                    result.getSongId()
            );
            result.setHlsLocalDir(hlsDir);
            log.info("HLS 변환 완료: {} (ID: {})", result.getTitle(), result.getSongId());
        } catch (Exception e) {
            log.error("HLS 변환 실패: {} by {}", result.getTitle(), result.getArtist(), e);
            markAsFailed(result, "HLS 변환 실패: " + e.getMessage());
        }
    }

    private void markAsFailed(SongProcessingResult result, String errorMessage) {
        result.setSuccess(false);
        result.setErrorMessage(errorMessage);
    }
}
