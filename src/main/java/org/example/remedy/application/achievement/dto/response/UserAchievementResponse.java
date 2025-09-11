package org.example.remedy.application.achievement.dto.response;

import org.example.remedy.domain.achievement.Achievement;
import org.example.remedy.domain.achievement.UserAchievement;

import java.time.LocalDateTime;

/**
 * 사용자별 도전과제 진행 상황 응답 DTO
 * 도전과제 정보와 사용자의 진행 상황을 조합하여 반환
 */
public record UserAchievementResponse(
        Long userAchievementId,    // 사용자 도전과제 ID
        Long achievementId,        // 도전과제 ID
        String title,              // 도전과제 제목
        Integer targetValue,       // 목표값
        Integer currentProgress,   // 현재 진행도
        double progressPercentage, // 진행률 (%)
        Integer rewardAmount,      // 보상 재화량
        boolean isCompleted,       // 완료 여부
        boolean isRewardClaimed,   // 보상 수령 여부
        LocalDateTime completedAt,     // 완료 시간
        LocalDateTime rewardClaimedAt  // 보상 수령 시간
) {
    /**
     * UserAchievement와 Achievement 엔티티를 조합하여 응답 DTO 생성
     * @param userAchievement 사용자 도전과제 진행 상황
     * @param achievement 도전과제 정보
     * @return 사용자 도전과제 응답 DTO
     */
    public static UserAchievementResponse from(UserAchievement userAchievement, Achievement achievement) {
        return new UserAchievementResponse(
                userAchievement.getUserAchievementId(),
                achievement.getAchievementId(),
                achievement.getTitle(),
                achievement.getTargetValue(),
                userAchievement.getCurrentProgress(),
                userAchievement.getProgressPercentage(achievement.getTargetValue()),
                achievement.getRewardAmount(),
                userAchievement.isCompleted(),
                userAchievement.isRewardClaimed(),
                userAchievement.getCompletedAt(),
                userAchievement.getRewardClaimedAt()
        );
    }
}