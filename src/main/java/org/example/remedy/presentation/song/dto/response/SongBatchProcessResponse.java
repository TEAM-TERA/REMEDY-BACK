package org.example.remedy.presentation.song.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.example.remedy.application.song.SongBatchProcessingService;

import java.util.List;

@Getter
@Builder
public class SongBatchProcessResponse {
    private int totalCount;
    private int successCount;
    private int failureCount;
    private List<SongProcessResultDto> results;

    @Getter
    @Builder
    public static class SongProcessResultDto {
        private boolean success;
        private String songId;
        private String title;
        private String artist;
        private String hlsPath;
        private String albumImagePath;
        private String errorMessage;

        public static SongProcessResultDto from(SongBatchProcessingService.SongProcessingResult result) {
            return SongProcessResultDto.builder()
                    .success(result.isSuccess())
                    .songId(result.getSongId())
                    .title(result.getTitle())
                    .artist(result.getArtist())
                    .hlsPath(result.getHlsPath())
                    .albumImagePath(result.getAlbumImagePath())
                    .errorMessage(result.getErrorMessage())
                    .build();
        }
    }

    public static SongBatchProcessResponse from(List<SongBatchProcessingService.SongProcessingResult> results) {
        List<SongProcessResultDto> resultDtos = results.stream()
                .map(SongProcessResultDto::from)
                .toList();

        int successCount = (int) results.stream().filter(SongBatchProcessingService.SongProcessingResult::isSuccess).count();
        int failureCount = results.size() - successCount;

        return SongBatchProcessResponse.builder()
                .totalCount(results.size())
                .successCount(successCount)
                .failureCount(failureCount)
                .results(resultDtos)
                .build();
    }
}