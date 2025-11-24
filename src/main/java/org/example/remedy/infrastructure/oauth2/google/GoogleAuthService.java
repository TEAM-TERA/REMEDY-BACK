package org.example.remedy.infrastructure.oauth2.google;

import lombok.RequiredArgsConstructor;
import org.example.remedy.infrastructure.oauth2.OAuth2AuthService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class GoogleAuthService implements OAuth2AuthService {
	private final GoogleAuthClient googleAuthClient;

	@Override
	public Map<String, Object> getUserInfo(String accessToken) {
		return googleAuthClient.getUserInfo("Bearer " + accessToken);
	}
}
