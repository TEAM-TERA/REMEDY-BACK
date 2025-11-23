package org.example.remedy.domain.oauth2.presentation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.remedy.domain.oauth2.application.dto.request.GoogleAuthRequest;
import org.example.remedy.domain.oauth2.application.dto.request.KakaoAuthRequest;
import org.example.remedy.domain.oauth2.application.dto.request.NaverAuthRequest;
import org.example.remedy.domain.oauth2.application.dto.response.OAuth2LoginResponse;
import org.example.remedy.domain.oauth2.application.service.OAuth2AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/oauth2")
public class OAuth2AuthController {
    private final OAuth2AuthService oAuth2AuthService;

    @PostMapping("/google")
	@ResponseStatus(HttpStatus.OK)
    public OAuth2LoginResponse googleLogin(@RequestBody GoogleAuthRequest request) {
        log.info("Google OAuth2 요청");
        return oAuth2AuthService.googleLogin(request);
    }

    @PostMapping("/kakao")
	@ResponseStatus(HttpStatus.OK)
    public OAuth2LoginResponse kakaoLogin(@RequestBody KakaoAuthRequest request) {
        log.info("Kakao OAuth2 요청");
        return oAuth2AuthService.kakaoLogin(request);
    }

    @PostMapping("/naver")
	@ResponseStatus(HttpStatus.OK)
    public OAuth2LoginResponse naverLogin(@RequestBody NaverAuthRequest request) {
        log.info("Naver OAuth2 요청");
        return oAuth2AuthService.naverLogin(request);
    }
}
