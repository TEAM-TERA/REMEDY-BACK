package org.example.remedy.domain.dropping.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record VoteRequest(
        @NotBlank(message = "투표할 음악 ID는 필수입니다")
        String songId
) { }