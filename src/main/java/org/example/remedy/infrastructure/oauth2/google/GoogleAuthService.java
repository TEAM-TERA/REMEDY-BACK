package org.example.remedy.infrastructure.oauth2.google;

import lombok.RequiredArgsConstructor;
import org.example.remedy.infrastructure.oauth2.OAuth2AuthService;
import org.example.remedy.infrastructure.oauth2.google.dto.response.GoogleTokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class GoogleAuthService implements OAuth2AuthService {
	private final GoogleAuthClient googleAuthClient;

	@Value("${google.client-id}")
	private String googleClientId;

	@Value("${google.client-secret}")
	private String googleClientSecret;

	@Value("${google.redirect-uri}")
	private String googleRedirectUri;

	public String getAccessToken(String authCode) {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("code", authCode);
		params.add("client_id", googleClientId);
		params.add("client_secret", googleClientSecret);
		params.add("redirect_uri", googleRedirectUri);
		params.add("grant_type", "authorization_code");

		GoogleTokenResponse googleTokenResponse = googleAuthClient.getToken(params);
		return googleTokenResponse.access_token();
	}

	@Override
	public Map<String, Object> getUserInfo(String accessToken) {
		return googleAuthClient.getUserInfo("Bearer " + accessToken);
	}
}
