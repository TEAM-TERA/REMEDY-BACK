package org.example.remedy.application.user;

import org.example.remedy.domain.user.User;
import org.example.remedy.interfaces.user.dto.response.UserProfileImageResponse;
import org.example.remedy.interfaces.user.dto.response.UserProfileResponse;

public class UserMapper {
    public static UserProfileImageResponse toUserProfileImageResponse(User user) {
        return new UserProfileImageResponse(
                user.getProfileImage()
        );
    }

    public static UserProfileResponse toUserProfileResponse(User user) {
        return new UserProfileResponse(
                user.getUsername(),
                user.getProfileImage()
        );
    }
}
