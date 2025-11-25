package org.example.remedy.domain.user.application.mapper;

import org.example.remedy.domain.user.application.dto.response.UserProfileImageResponse;
import org.example.remedy.domain.user.application.dto.response.UserProfileResponse;
import org.example.remedy.domain.user.domain.User;

public class UserMapper {
    public static UserProfileImageResponse toUserProfileImageResponse(User user) {
        return new UserProfileImageResponse(
                user.getProfileImage()
        );
    }

    public static UserProfileResponse toUserProfileResponse(User user) {
        return new UserProfileResponse(
                user.getUsername(),
                user.getProfileImage(),
                user.getGender(),
                user.getBirthDate()
        );
    }
}
