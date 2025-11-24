package org.example.remedy.domain.playlist.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record PlaylistSongAddRequest(
        @NotBlank(message = "곡 ID는 필수입니다.")
        String songId
) {
}