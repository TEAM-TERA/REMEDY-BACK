package org.example.remedy.presentation.user.dto.request;

import jakarta.validation.constraints.NotBlank;

public record FcmTokenRequest(
        @NotBlank(message = "FCM 토큰은 필수입니다.")
        String token
) {}
