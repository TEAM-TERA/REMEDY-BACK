package org.example.remedy.infrastructure.oauth2.naver;

import lombok.RequiredArgsConstructor;
import org.example.remedy.infrastructure.oauth2.OAuth2AuthService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class NaverAuthService implements OAuth2AuthService {
	private final NaverAuthClient naverAuthClient;

	@Override
	public Map<String, Object> getUserInfo(String accessToken) {
		return naverAuthClient.getUserInfo(
			"Bearer " + accessToken,
			"application/x-www-form-urlencoded"
		);
	}
}
