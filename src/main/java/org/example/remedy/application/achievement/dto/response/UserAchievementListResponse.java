package org.example.remedy.application.achievement.dto.response;

import java.util.List;

/**
 * 사용자 도전과제 목록 응답 DTO
 * 
 * 사용자의 도전과제 목록과 통계 정보를 제공합니다.
 * 
 * @param achievements 사용자 도전과제 목록
 * @param totalCount 전체 도전과제 수
 * @param completedCount 완료된 도전과제 수
 * @param unclaimedRewardCount 보상을 받지 않은 완료된 도전과제 수
 */
public record UserAchievementListResponse(
        List<UserAchievementResponse> achievements,
        int totalCount,
        int completedCount,
        int unclaimedRewardCount
) {
    /**
     * 사용자 도전과제 목록으로부터 응답 객체를 생성합니다.
     * 
     * @param achievements 사용자 도전과제 응답 목록
     * @return 통계 정보가 포함된 사용자 도전과제 목록 응답
     */
    public static UserAchievementListResponse from(List<UserAchievementResponse> achievements) {
        int completedCount = (int) achievements.stream().filter(UserAchievementResponse::isCompleted).count();
        int unclaimedRewardCount = (int) achievements.stream()
                .filter(a -> a.isCompleted() && !a.isRewardClaimed())
                .count();
        
        return new UserAchievementListResponse(
                achievements,
                achievements.size(),
                completedCount,
                unclaimedRewardCount
        );
    }
}