package org.example.remedy.interfaces.user.dto.response;

public record UserProfileImageResponse(
        String profileImageUrl
) {
    public static UserProfileImageResponse newInstance(String userProfileImageUrl) {
        return new UserProfileImageResponse(
                userProfileImageUrl
        );
    }
}
