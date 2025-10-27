package org.example.remedy.application.song.dto.response;

import java.util.List;

public record SongBatchDownloadResponse(
        int totalCount,
        int successCount,
        int failureCount,
        List<SongDownloadResponse> results
) {
    public static SongBatchDownloadResponse from(List<SongDownloadResponse> results) {
        int successCount = (int) results.stream().filter(SongDownloadResponse::isSuccess).count();
        int failureCount = results.size() - successCount;

        return new SongBatchDownloadResponse(
                results.size(),
                successCount,
                failureCount,
                results
        );
    }
}
