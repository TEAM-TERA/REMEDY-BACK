package org.example.remedy.domain.auth.application.dto.request;

import jakarta.validation.constraints.*;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record AuthRegisterRequest(
        @Size(min = 1, max = 15, message = "닉네임은 최소 1자 이상, 최대 15자 이하여야 합니다.")
        @NotBlank(message = "닉네임 입력은 필수입니다.")
        String username,

        @Size(min = 8,  max = 20, message = "비밀번호는 최소 8자 이상, 최대 20자 이하여야 합니다.")
        @NotBlank(message = "비밀번호 입력은 필수입니다.")
        String password,

        @Email(message = "이메일 형식이 올바르지 않습니다.")
        @NotBlank(message = "이메일 입력은 필수입니다.")
        String email,

		@Past(message = "생년월일은 미래일 수 없습니다.")
        @NotNull(message = "생년월일 입력은 필수입니다.")
        LocalDate birthDate,

		@NotNull(message = "성별 입력은 필수 입니다.")
        Boolean gender // true : 남성, false : 여성
) { }
