package org.example.remedy.application.achievement.dto.response;

import java.util.List;

/**
 * 페이징된 사용자 도전과제 목록 응답 DTO
 *
 * 사용자의 도전과제 목록을 페이징 처리하여 제공합니다.
 *
 * @param achievements 현재 페이지의 사용자 도전과제 목록
 * @param totalCount 전체 도전과제 수
 * @param completedCount 완료된 도전과제 수
 * @param unclaimedRewardCount 보상을 받지 않은 완료된 도전과제 수
 * @param currentPage 현재 페이지 번호 (0부터 시작)
 * @param pageSize 페이지당 항목 수
 * @param totalPages 전체 페이지 수
 * @param hasNext 다음 페이지 존재 여부
 * @param hasPrevious 이전 페이지 존재 여부
 */
public record PagedUserAchievementListResponse(
        List<UserAchievementResponse> achievements,
        long totalCount,
        int completedCount,
        int unclaimedRewardCount,
        int currentPage,
        int pageSize,
        int totalPages,
        boolean hasNext,
        boolean hasPrevious
) {
    /**
     * 페이징된 사용자 도전과제 목록으로부터 응답 객체를 생성합니다.
     *
     * @param achievements 사용자 도전과제 응답 목록
     * @param totalCount 전체 도전과제 수
     * @param currentPage 현재 페이지 번호
     * @param pageSize 페이지당 항목 수
     * @return 통계 정보와 페이징 정보가 포함된 응답
     */
    public static PagedUserAchievementListResponse of(
            List<UserAchievementResponse> achievements,
            long totalCount,
            int currentPage,
            int pageSize
    ) {
        int completedCount = (int) achievements.stream().filter(UserAchievementResponse::isCompleted).count();
        int unclaimedRewardCount = (int) achievements.stream()
                .filter(a -> a.isCompleted() && !a.isRewardClaimed())
                .count();

        int totalPages = (int) Math.ceil((double) totalCount / pageSize);
        boolean hasNext = currentPage < totalPages - 1;
        boolean hasPrevious = currentPage > 0;

        return new PagedUserAchievementListResponse(
                achievements,
                totalCount,
                completedCount,
                unclaimedRewardCount,
                currentPage,
                pageSize,
                totalPages,
                hasNext,
                hasPrevious
        );
    }
}
