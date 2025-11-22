package org.example.remedy.domain.user.application.dto.response;

import java.time.LocalDate;

public record UserProfileResponse (
        String username,
        String profileImageUrl,
        boolean gender,
        LocalDate birth

) {}