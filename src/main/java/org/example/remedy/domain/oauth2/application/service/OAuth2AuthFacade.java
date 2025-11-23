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
import org.example.remedy.infrastructure.oauth2.google.GoogleAuthService;
import org.example.remedy.infrastructure.oauth2.kakao.KakaoAuthService;
import org.example.remedy.infrastructure.oauth2.naver.NaverAuthService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class OAuth2AuthFacade {
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
	private final GoogleAuthService googleAuthService;
	private final NaverAuthService naverAuthService;
	private final KakaoAuthService kakaoAuthService;

    public OAuth2LoginResponse googleLogin(GoogleAuthRequest request) {
        String accessToken = googleAuthService.getAccessToken(request.authCode());

        Map<String, Object> userAttributes = googleAuthService.getUserInfo(accessToken);

        OAuth2UserInfo oAuth2UserInfo = new GoogleOAuth2UserInfo(userAttributes);

        return processOAuth2Login(oAuth2UserInfo);
    }

    public OAuth2LoginResponse kakaoLogin(KakaoAuthRequest request) {
        Map<String, Object> userAttributes = kakaoAuthService.getUserInfo(request.accessToken());

        OAuth2UserInfo oAuth2UserInfo = new KakaoOAuth2UserInfo(userAttributes);

        return processOAuth2Login(oAuth2UserInfo);
    }

    public OAuth2LoginResponse naverLogin(NaverAuthRequest request) {
        Map<String, Object> userAttributes = naverAuthService.getUserInfo(request.accessToken());

        OAuth2UserInfo oAuth2UserInfo = new NaverOAuth2UserInfo(userAttributes);

        return processOAuth2Login(oAuth2UserInfo);
    }

    private OAuth2LoginResponse processOAuth2Login(OAuth2UserInfo oAuth2UserInfo) {
        OAuth2Provider provider = oAuth2UserInfo.getProvider();
        String providerId = oAuth2UserInfo.getProviderId();

        User user = userRepository.findByProviderAndProviderId(provider, providerId)
                .orElseGet(() -> createUser(oAuth2UserInfo));

        String jwtToken = tokenProvider.createAccessToken(user.getEmail());

        return OAuth2LoginResponse.of(jwtToken);
    }

	private User createUser(OAuth2UserInfo oAuth2UserInfo) {
		User newUser = Oauth2Mapper.toUserEntity(oAuth2UserInfo);
		return userRepository.save(newUser);
	}
}
