package org.example.remedy.infrastructure.oauth2;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.remedy.global.config.properties.OAuth2DeepLinkProperties;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {
    private final OAuth2DeepLinkProperties deepLinkProperties;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String errorMessage = exception.getMessage();
        String encodedErrorMessage = URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);

        // 딥링크 URL 생성: remedy://oauth/error?error={errorCode}&message={errorMessage}
        String targetUrl = UriComponentsBuilder
                .newInstance()
                .scheme(deepLinkProperties.getScheme())
                .host(deepLinkProperties.getPath())
                .path("/error")
                .queryParam("error", "AUTHENTICATION_FAILED")
                .queryParam("message", encodedErrorMessage)
                .build()
                .toUriString();

        log.error("OAuth2 authentication failed: {}. Redirecting to deep link: {}", errorMessage, targetUrl);

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
