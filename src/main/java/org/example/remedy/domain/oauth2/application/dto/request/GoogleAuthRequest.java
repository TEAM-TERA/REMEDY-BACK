package org.example.remedy.domain.oauth2.application.dto.request;

public record GoogleAuthRequest(
	String accessToken  // Google에서 받은 Authorization Code
) {
}
