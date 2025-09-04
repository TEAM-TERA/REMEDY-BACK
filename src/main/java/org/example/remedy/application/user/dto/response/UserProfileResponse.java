package org.example.remedy.application.user.dto.response;

public record UserProfileResponse (
        String username,
        String profileImageUrl
) {}