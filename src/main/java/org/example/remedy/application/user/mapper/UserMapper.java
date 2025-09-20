package org.example.remedy.application.user.mapper;

import org.example.remedy.domain.user.User;
import org.example.remedy.application.user.dto.response.UserProfileImageResponse;
import org.example.remedy.application.user.dto.response.UserProfileResponse;

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
                user.isGender(),
                user.getBirthDate()
        );
    }
}
