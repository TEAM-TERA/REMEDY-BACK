package org.example.remedy.domain.user.dto.response;

import org.example.remedy.domain.user.domain.User;

public record UserProfileImageResponse(
        String profileImageUrl
) {
    public static UserProfileImageResponse newInstance(User user) {
        return new UserProfileImageResponse(
                user.getProfileImage()
        );
    }
}
