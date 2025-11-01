package org.example.remedy.presentation.song.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record SongBatchDownloadRequest(
        @NotEmpty(message = "노래 제목 리스트는 비어있을 수 없습니다.")
        @Size(max = 10, message = "한 번에 최대 10곡까지 처리할 수 있습니다.")
        List<SongDownloadRequest> songDownloadRequests
) {

    public SongBatchDownloadRequest(List<SongDownloadRequest> songDownloadRequests) {
        this.songDownloadRequests = songDownloadRequests;
    }
}