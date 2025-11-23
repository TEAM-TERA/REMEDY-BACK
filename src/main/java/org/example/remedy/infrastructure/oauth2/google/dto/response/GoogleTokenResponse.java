package org.example.remedy.infrastructure.oauth2.google.dto.response;

public record GoogleTokenResponse(
	String access_token,
	String refresh_token,
	String scope,
	String token_type,
	Integer expires_in
) { }
