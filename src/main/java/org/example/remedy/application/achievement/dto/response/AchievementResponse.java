package org.example.remedy.application.achievement.dto.response;

import org.example.remedy.domain.achievement.Achievement;
import org.example.remedy.domain.achievement.AchievementPeriod;
import org.example.remedy.domain.achievement.AchievementType;

public record AchievementResponse(
        Long achievementId,
        String title,
        AchievementType type,
        String typeDescription,
        AchievementPeriod period, // 도전과제 기간 타입
        String periodDescription, // 도전과제 기간 설명
        Integer targetValue,
        Integer rewardAmount,
        boolean isActive
) {
    public static AchievementResponse from(Achievement achievement) {
        return new AchievementResponse(
                achievement.getAchievementId(),
                achievement.getTitle(),
                achievement.getType(),
                achievement.getType().getDescription(),
                achievement.getPeriod(),
                achievement.getPeriod().getDescription(),
                achievement.getTargetValue(),
                achievement.getRewardAmount(),
                achievement.isActive()
        );
    }
}