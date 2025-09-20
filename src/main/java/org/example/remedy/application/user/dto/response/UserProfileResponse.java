package org.example.remedy.application.user.dto.response;

import java.time.LocalDate;

public record UserProfileResponse (
        String username,
        String profileImageUrl,
        boolean gender,
        LocalDate birth

) {}