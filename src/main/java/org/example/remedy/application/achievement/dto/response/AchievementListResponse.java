package org.example.remedy.application.achievement.dto.response;

import org.example.remedy.domain.achievement.Achievement;

import java.util.List;

/**
 * 도전과제 목록 응답 DTO
 * 여러 도전과제 정보와 총 개수를 함께 반환
 */
public record AchievementListResponse(
        List<AchievementResponse> achievements, // 도전과제 목록
        int totalCount // 총 도전과제 개수
) {
    /**
     * Achievement 엔티티 목록을 응답 DTO로 변환
     * @param achievements 도전과제 엔티티 목록
     * @return 도전과제 목록 응답 DTO
     */
    public static AchievementListResponse from(List<Achievement> achievements) {
        List<AchievementResponse> achievementResponses = achievements.stream()
                .map(AchievementResponse::from)
                .toList();
        
        return new AchievementListResponse(achievementResponses, achievements.size());
    }
}