package org.example.remedy.interfaces.user.dto.response;

import org.example.remedy.domain.user.User;

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