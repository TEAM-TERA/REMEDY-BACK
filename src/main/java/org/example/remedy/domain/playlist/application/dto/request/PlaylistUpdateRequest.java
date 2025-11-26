package org.example.remedy.domain.playlist.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record PlaylistUpdateRequest(
        @NotBlank(message = "플레이리스트 이름은 필수입니다.")
        String name
) {
}