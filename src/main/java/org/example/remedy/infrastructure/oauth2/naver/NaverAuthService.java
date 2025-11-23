package org.example.remedy.infrastructure.oauth2.naver;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class NaverAuthService {
	private final NaverAuthClient naverAuthClient;

	public Map<String, Object> getUserInfo(String accessToken) {
		return naverAuthClient.getUserInfo(
			"Bearer " + accessToken,
			"application/x-www-form-urlencoded"
		);
	}
}
