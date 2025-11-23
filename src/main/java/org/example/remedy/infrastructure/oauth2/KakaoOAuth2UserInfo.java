package org.example.remedy.infrastructure.oauth2;

import org.example.remedy.domain.user.domain.OAuth2Provider;

import java.time.LocalDate;
import java.util.Map;

public class KakaoOAuth2UserInfo implements OAuth2UserInfo {
    private final Map<String, Object> attributes;

    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public OAuth2Provider getProvider() {
        return OAuth2Provider.KAKAO;
    }

    @Override
    public String getProviderId() {
        return String.valueOf(attributes.get("id"));
    }

    @Override
    public String getEmail() {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        if (kakaoAccount == null) {
            return null;
        }
        return (String) kakaoAccount.get("email");
    }

    @Override
    public String getName() {
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
        if (properties == null) {
            return null;
        }
        return (String) properties.get("nickname");
    }

    @Override
    public String getProfileImage() {
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
        if (properties == null) {
            return null;
        }
        return (String) properties.get("profile_image");
    }

    @Override
    public LocalDate getBirthDate() {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        if (kakaoAccount == null) {
            return null;
        }

        String birthday = (String) kakaoAccount.get("birthday"); // MMDD 형식
        String birthyear = (String) kakaoAccount.get("birthyear"); // YYYY 형식

        if (birthday != null && birthyear != null && birthday.length() == 4 && birthyear.length() == 4) {
            try {
                int year = Integer.parseInt(birthyear);
                int month = Integer.parseInt(birthday.substring(0, 2));
                int day = Integer.parseInt(birthday.substring(2, 4));
                return LocalDate.of(year, month, day);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    @Override
    public Boolean getGender() {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        if (kakaoAccount == null) {
            return null;
        }

        String gender = (String) kakaoAccount.get("gender");
        if (gender == null) {
            return null;
        }

        // Kakao: "male" 또는 "female"
        return gender.equalsIgnoreCase("male");
    }
}
