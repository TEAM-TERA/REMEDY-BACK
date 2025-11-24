package org.example.remedy.domain.oauth2.application.dto.request;

public record KakaoAuthRequest(
        String accessToken  // Kakao SDK에서 받은 Access Token
) {
}
