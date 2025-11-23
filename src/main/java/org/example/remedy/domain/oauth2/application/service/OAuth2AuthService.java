package org.example.remedy.domain.oauth2.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.remedy.domain.oauth2.application.dto.request.GoogleAuthRequest;
import org.example.remedy.domain.oauth2.application.dto.request.KakaoAuthRequest;
import org.example.remedy.domain.oauth2.application.dto.request.NaverAuthRequest;
import org.example.remedy.domain.oauth2.application.dto.response.OAuth2LoginResponse;
import org.example.remedy.domain.user.domain.OAuth2Provider;
import org.example.remedy.domain.user.domain.User;
import org.example.remedy.domain.user.repository.UserRepository;
import org.example.remedy.global.security.jwt.TokenProvider;
import org.example.remedy.domain.oauth2.application.mapper.Oauth2Mapper;
import org.example.remedy.domain.oauth2.domain.GoogleOAuth2UserInfo;
import org.example.remedy.domain.oauth2.domain.KakaoOAuth2UserInfo;
import org.example.remedy.domain.oauth2.domain.NaverOAuth2UserInfo;
import org.example.remedy.domain.oauth2.domain.OAuth2UserInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class OAuth2AuthService {
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final RestTemplate restTemplate;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String googleRedirectUri;

    public OAuth2LoginResponse googleLogin(GoogleAuthRequest request) {
        // 1. Authorization Code로 Access Token 교환
        String accessToken = getGoogleAccessToken(request.authCode());

        // 2. Access Token으로 사용자 정보 조회
        Map<String, Object> userAttributes = getGoogleUserInfo(accessToken);

        // 3. OAuth2UserInfo 생성
        OAuth2UserInfo oAuth2UserInfo = new GoogleOAuth2UserInfo(userAttributes);

        // 4. User 처리 및 JWT 생성
        return processOAuth2Login(oAuth2UserInfo);
    }

    public OAuth2LoginResponse kakaoLogin(KakaoAuthRequest request) {
        // 1. Access Token으로 사용자 정보 조회
        Map<String, Object> userAttributes = getKakaoUserInfo(request.accessToken());

        // 2. OAuth2UserInfo 생성
        OAuth2UserInfo oAuth2UserInfo = new KakaoOAuth2UserInfo(userAttributes);

        // 3. User 처리 및 JWT 생성
        return processOAuth2Login(oAuth2UserInfo);
    }

    public OAuth2LoginResponse naverLogin(NaverAuthRequest request) {
        // 1. Access Token으로 사용자 정보 조회
        Map<String, Object> userAttributes = getNaverUserInfo(request.accessToken());

        // 2. OAuth2UserInfo 생성
        OAuth2UserInfo oAuth2UserInfo = new NaverOAuth2UserInfo(userAttributes);

        // 3. User 처리 및 JWT 생성
        return processOAuth2Login(oAuth2UserInfo);
    }

    private OAuth2LoginResponse processOAuth2Login(OAuth2UserInfo oAuth2UserInfo) {
        OAuth2Provider provider = oAuth2UserInfo.getProvider();
        String providerId = oAuth2UserInfo.getProviderId();

        // User 조회 또는 생성
        User user = userRepository.findByProviderAndProviderId(provider, providerId)
                .orElseGet(() -> createUser(oAuth2UserInfo));

        // JWT 생성
        String jwtToken = tokenProvider.createAccessToken(user.getEmail());

        return OAuth2LoginResponse.of(jwtToken);
    }

	private User createUser(OAuth2UserInfo oAuth2UserInfo) {
		User newUser = Oauth2Mapper.toUserEntity(oAuth2UserInfo);
		userRepository.save(newUser);
		return newUser;
	}

    private String getGoogleAccessToken(String authCode) {
        String tokenUrl = "https://oauth2.googleapis.com/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", authCode);
        params.add("client_id", googleClientId);
        params.add("client_secret", googleClientSecret);
        params.add("redirect_uri", googleRedirectUri);
        params.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);

        return (String) response.getBody().get("access_token");
    }

    private Map<String, Object> getGoogleUserInfo(String accessToken) {
        String userInfoUrl = "https://www.googleapis.com/oauth2/v2/userinfo";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(userInfoUrl, HttpMethod.GET, request, Map.class);

        return response.getBody();
    }

    private Map<String, Object> getKakaoUserInfo(String accessToken) {
        String userInfoUrl = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(userInfoUrl, HttpMethod.GET, request, Map.class);

        return response.getBody();
    }

    private Map<String, Object> getNaverUserInfo(String accessToken) {
        String userInfoUrl = "https://openapi.naver.com/v1/nid/me";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(userInfoUrl, HttpMethod.GET, request, Map.class);

        return response.getBody();
    }
}
