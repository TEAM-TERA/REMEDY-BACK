package org.example.remedy.infrastructure.oauth2;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.remedy.global.config.properties.OAuth2DeepLinkProperties;
import org.example.remedy.global.security.jwt.TokenProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final TokenProvider tokenProvider;
    private final OAuth2DeepLinkProperties deepLinkProperties;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2UserPrincipal principal = (OAuth2UserPrincipal) authentication.getPrincipal();
        String email = principal.getUser().getEmail();

        // JWT 토큰 생성
        String accessToken = tokenProvider.createAccessToken(email);

        // OAuth2 제공자 정보 추출
        String provider = "";
        if (authentication instanceof OAuth2AuthenticationToken) {
            provider = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();
        }

        // 딥링크 URL 생성: remedy://oauth/{provider}?token={accessToken}
        String targetUrl = UriComponentsBuilder
                .newInstance()
                .scheme(deepLinkProperties.getScheme())
                .host(deepLinkProperties.getPath())
                .path("/" + provider)
                .queryParam("token", accessToken)
                .build()
                .toUriString();

        log.info("OAuth2 authentication success. Redirecting to deep link: {}", targetUrl);

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
