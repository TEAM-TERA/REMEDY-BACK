package org.example.remedy.domain.playlist.application.dto.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record PlaylistSongAddRequest(
        @NotEmpty(message = "곡 ID 목록은 필수입니다.")
        List<String> songIds
) {
}