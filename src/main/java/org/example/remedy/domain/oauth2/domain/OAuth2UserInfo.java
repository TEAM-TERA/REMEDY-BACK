package org.example.remedy.domain.oauth2.domain;

import org.example.remedy.domain.user.domain.OAuth2Provider;

import java.time.LocalDate;

public interface OAuth2UserInfo {
    OAuth2Provider getProvider();
    String getProviderId();
    String getEmail();
    String getName();
    String getProfileImage();
    LocalDate getBirthDate();
    Boolean getGender(); // true: 남성, false: 여성, null: 제공 안 함
}
