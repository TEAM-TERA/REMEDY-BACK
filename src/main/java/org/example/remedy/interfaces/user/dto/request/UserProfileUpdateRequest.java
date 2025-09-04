package org.example.remedy.interfaces.user.dto.request;

import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UserProfileUpdateRequest(
        @Size(max = 15, message = "닉네임은 최대 15자 이하여야 합니다.")
        String username,
        Boolean gender,
        LocalDate birthDate
) {}