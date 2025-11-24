package org.example.remedy.infrastructure.oauth2.google;

import org.example.remedy.infrastructure.oauth2.google.dto.response.GoogleTokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

@FeignClient(name = "google-auth", url = "https://oauth2.googleapis.com")
public interface GoogleAuthClient {
	@PostMapping(value = "/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	GoogleTokenResponse getToken(@RequestBody MultiValueMap<String, String> params);

	@GetMapping("/oauth2/v2/userinfo")
	Map<String, Object> getUserInfo(@RequestHeader("Authorization") String bearerToken);
}
