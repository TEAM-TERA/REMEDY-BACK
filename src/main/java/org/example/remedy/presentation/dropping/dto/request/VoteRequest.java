package org.example.remedy.presentation.dropping.dto.request;

import jakarta.validation.constraints.NotBlank;

public record VoteRequest(
        @NotBlank(message = "투표할 옵션은 필수입니다")
        String optionText
) { }