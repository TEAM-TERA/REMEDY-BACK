package org.example.remedy.presentation.song.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class SongBatchProcessRequest {

    @NotEmpty(message = "노래 제목 리스트는 비어있을 수 없습니다.")
    @Size(max = 10, message = "한 번에 최대 10곡까지 처리할 수 있습니다.")
    private List<String> songTitles;

    public SongBatchProcessRequest(List<String> songTitles) {
        this.songTitles = songTitles;
    }
}