package org.example.remedy.domain.oauth2.presentation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.remedy.domain.oauth2.application.dto.request.GoogleAuthRequest;
import org.example.remedy.domain.oauth2.application.dto.request.KakaoAuthRequest;
import org.example.remedy.domain.oauth2.application.dto.request.NaverAuthRequest;
import org.example.remedy.domain.oauth2.application.dto.response.OAuth2LoginResponse;
import org.example.remedy.domain.oauth2.application.service.OAuth2AuthFacade;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/oauth2")
public class OAuth2AuthController {
    private final OAuth2AuthFacade oAuth2AuthFacade;

    @PostMapping("/google")
	@ResponseStatus(HttpStatus.OK)
    public OAuth2LoginResponse googleLogin(@RequestBody GoogleAuthRequest request) {
        log.info("Google OAuth2 요청");
        return oAuth2AuthFacade.googleLogin(request);
    }

    @PostMapping("/kakao")
	@ResponseStatus(HttpStatus.OK)
    public OAuth2LoginResponse kakaoLogin(@RequestBody KakaoAuthRequest request) {
        log.info("Kakao OAuth2 요청");
        return oAuth2AuthFacade.kakaoLogin(request);
    }

    @PostMapping("/naver")
	@ResponseStatus(HttpStatus.OK)
    public OAuth2LoginResponse naverLogin(@RequestBody NaverAuthRequest request) {
        log.info("Naver OAuth2 요청");
        return oAuth2AuthFacade.naverLogin(request);
    }
}
