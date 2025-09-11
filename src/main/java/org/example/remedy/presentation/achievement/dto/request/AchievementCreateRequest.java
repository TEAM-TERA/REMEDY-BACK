package org.example.remedy.presentation.achievement.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.example.remedy.domain.achievement.AchievementPeriod;
import org.example.remedy.domain.achievement.AchievementType;

public record AchievementCreateRequest(
        @NotBlank(message = "도전과제 제목은 필수입니다.")
        @Size(max = 100, message = "도전과제 제목은 최대 100자까지 입력 가능합니다.")
        String title,

        @NotNull(message = "도전과제 타입은 필수입니다.")
        AchievementType type,

        @NotNull(message = "도전과제 기간은 필수입니다.")
        AchievementPeriod period, // 도전과제 기간 (일일/상시)

        @NotNull(message = "목표값은 필수입니다.")
        @Min(value = 1, message = "목표값은 1 이상이어야 합니다.")
        Integer targetValue,

        @NotNull(message = "보상 금액은 필수입니다.")
        @Min(value = 0, message = "보상 금액은 0 이상이어야 합니다.")
        Integer rewardAmount
) {}