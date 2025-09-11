package org.example.remedy.presentation.achievement.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 * 도전과제 수정 요청 DTO
 * 
 * 관리자가 도전과제 정보를 수정할 때 사용하는 요청 객체입니다.
 * 
 * @param title 도전과제 제목 (최대 100자)
 * @param targetValue 목표값 (1 이상)
 * @param rewardAmount 보상 금액 (0 이상)
 */
public record AchievementUpdateRequest(
        @Size(max = 100, message = "도전과제 제목은 최대 100자까지 입력 가능합니다.")
        String title,

        @Min(value = 1, message = "목표값은 1 이상이어야 합니다.")
        Integer targetValue,

        @Min(value = 0, message = "보상 금액은 0 이상이어야 합니다.")
        Integer rewardAmount
) {}