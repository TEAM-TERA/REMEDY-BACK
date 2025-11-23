package org.example.remedy.infrastructure.oauth2;

import org.example.remedy.domain.user.domain.OAuth2Provider;

import java.time.LocalDate;
import java.util.Map;

public class NaverOAuth2UserInfo implements OAuth2UserInfo {
    private final Map<String, Object> attributes;

    public NaverOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public OAuth2Provider getProvider() {
        return OAuth2Provider.NAVER;
    }

    @Override
    public String getProviderId() {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        if (response == null) {
            return null;
        }
        return (String) response.get("id");
    }

    @Override
    public String getEmail() {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        if (response == null) {
            return null;
        }
        return (String) response.get("email");
    }

    @Override
    public String getName() {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        if (response == null) {
            return null;
        }
        return (String) response.get("nickname");
    }

    @Override
    public String getProfileImage() {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        if (response == null) {
            return null;
        }
        return (String) response.get("profile_image");
    }

    @Override
    public LocalDate getBirthDate() {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        if (response == null) {
            return null;
        }

        String birthday = (String) response.get("birthday"); // MM-DD 형식
        String birthyear = (String) response.get("birthyear"); // YYYY 형식

        if (birthday != null && birthyear != null && birthday.length() == 5 && birthyear.length() == 4) {
            try {
                int year = Integer.parseInt(birthyear);
                String[] parts = birthday.split("-");
                if (parts.length == 2) {
                    int month = Integer.parseInt(parts[0]);
                    int day = Integer.parseInt(parts[1]);
                    return LocalDate.of(year, month, day);
                }
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    @Override
    public Boolean getGender() {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        if (response == null) {
            return null;
        }

        String gender = (String) response.get("gender");
        if (gender == null) {
            return null;
        }

        // Naver: "M" 또는 "F"
        return gender.equalsIgnoreCase("M");
    }
}

