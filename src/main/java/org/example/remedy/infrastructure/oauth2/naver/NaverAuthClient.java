package org.example.remedy.infrastructure.oauth2.naver;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

@FeignClient(url = "https://openapi.naver.com")
public interface NaverAuthClient {
	@GetMapping("/v2/nid/me")
	Map<String, Object> getUserInfo(
		@RequestHeader("Authorization") String bearerToken,
		@RequestHeader("Content-Type") String contentType
	);
}
