package org.example.remedy.infrastructure.oauth2.kakao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class KakaoAuthService {
	private final KakaoAuthClient kakaoAuthClient;

	public Map<String, Object> getUserInfo(String accessToken) {
		return kakaoAuthClient.getUserInfo(
			"Bearer " + accessToken,
			"application/x-www-form-urlencoded"
		);
	}
}
