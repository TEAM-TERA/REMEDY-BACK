package org.example.remedy.infrastructure.oauth2.google;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

@FeignClient(name = "google-auth", url = "https://oauth2.googleapis.com")
public interface GoogleAuthClient {
	@GetMapping("/oauth2/v2/userinfo")
	Map<String, Object> getUserInfo(@RequestHeader("Authorization") String bearerToken);
}
