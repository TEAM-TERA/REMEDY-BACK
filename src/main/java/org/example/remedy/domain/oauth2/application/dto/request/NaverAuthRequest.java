package org.example.remedy.domain.oauth2.application.dto.request;

public record NaverAuthRequest(
        String accessToken  // Naver SDK에서 받은 Access Token
) {
}
