package org.example.remedy.interfaces.user.dto.response;

import org.example.remedy.domain.user.User;

public record UserProfileImageResponse(
        String profileImageUrl
) {
    public static UserProfileImageResponse newInstance(User user) {
        return new UserProfileImageResponse(
                user.getProfileImage()
        );
    }
}
