package org.example.remedy.domain.oauth2.application.dto.response;

public record OAuth2LoginResponse(
        String accessToken
) {
    public static OAuth2LoginResponse of(String accessToken) {
        return new OAuth2LoginResponse(accessToken);
    }
}
