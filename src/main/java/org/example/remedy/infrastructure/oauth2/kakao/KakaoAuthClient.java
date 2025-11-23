package org.example.remedy.infrastructure.oauth2.kakao;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

@FeignClient(url = "https://kapi.kakao.com")
public interface KakaoAuthClient {
	@GetMapping("/v2/user/me")
	Map<String, Object> getUserInfo(
		@RequestHeader("Authorization") String bearerToken,
		@RequestHeader("Content-Type") String contentType
	);


}
