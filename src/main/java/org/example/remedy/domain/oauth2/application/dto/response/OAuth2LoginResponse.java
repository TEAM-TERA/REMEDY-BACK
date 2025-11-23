package org.example.remedy.domain.oauth2.application.dto.response;

public record OAuth2LoginResponse(
        String accessToken,
        String tokenType,
        Long expiresIn
) {
    public static OAuth2LoginResponse of(String accessToken, String tokenType, Long expiresIn) {
        return new OAuth2LoginResponse(accessToken, tokenType, expiresIn);
    }
}
