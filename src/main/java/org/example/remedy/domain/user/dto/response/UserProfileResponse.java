package org.example.remedy.domain.user.dto.response;

import org.example.remedy.domain.user.domain.User;

public record UserProfileResponse (
        String username,
        String profileImageUrl
){
    public static UserProfileResponse newInstance(User user) {
        return new UserProfileResponse(
                user.getUsername(),
                user.getProfileImage()
        );
    }
}