package org.example.remedy.domain.oauth2.domain;

import org.example.remedy.domain.user.domain.OAuth2Provider;

import java.time.LocalDate;
import java.util.Map;

public class GoogleOAuth2UserInfo implements OAuth2UserInfo {
    private final Map<String, Object> attributes;

    public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public OAuth2Provider getProvider() {
        return OAuth2Provider.GOOGLE;
    }

    @Override
    public String getProviderId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getName() {
        // given_name (이름)을 우선 사용, 없으면 name (전체 이름) 사용
        String givenName = (String) attributes.get("given_name");
        if (givenName != null && !givenName.isEmpty()) {
            return givenName;
        }
        return (String) attributes.get("name");
    }

    @Override
    public String getProfileImage() {
        return (String) attributes.get("picture");
    }

    @Override
    public LocalDate getBirthDate() {
        // Google은 생년월일을 제공하지 않음 (별도 API 필요)
        return null;
    }

    @Override
    public Boolean getGender() {
        // Google은 기본 profile scope에서 성별을 제공하지 않음
        return null;
    }
}
