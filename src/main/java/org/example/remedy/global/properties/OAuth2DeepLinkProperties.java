package org.example.remedy.global.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "oauth2.deep-link")
public class OAuth2DeepLinkProperties {
    private final String scheme;
    private final String path;
}
