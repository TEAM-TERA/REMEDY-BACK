package org.example.remedy.interfaces.user.dto.response;

public record UserProfileResponse (
        String username,
        String profileImageUrl
) {}