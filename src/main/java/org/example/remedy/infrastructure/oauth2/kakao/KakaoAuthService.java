package org.example.remedy.infrastructure.oauth2.kakao;

import lombok.RequiredArgsConstructor;
import org.example.remedy.infrastructure.oauth2.OAuth2AuthService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class KakaoAuthService implements OAuth2AuthService {
	private final KakaoAuthClient kakaoAuthClient;

	@Override
	public Map<String, Object> getUserInfo(String accessToken) {
		return kakaoAuthClient.getUserInfo(
			"Bearer " + accessToken,
			"application/x-www-form-urlencoded"
		);
	}
}
