package org.example.remedy.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UserProfileUpdateRequest(
        @NotBlank(message = "닉네임은 필수입니다.")
        String username,


        Boolean gender
) {}