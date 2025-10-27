package org.example.remedy.application.achievement.port.in;

import org.example.remedy.application.achievement.dto.response.AchievementListResponse;
import org.example.remedy.application.achievement.dto.response.AchievementResponse;
import org.example.remedy.application.achievement.dto.response.PagedUserAchievementListResponse;
import org.example.remedy.application.achievement.dto.response.UserAchievementListResponse;
import org.example.remedy.application.achievement.dto.response.UserAchievementResponse;
import org.example.remedy.domain.achievement.AchievementPeriod;
import org.example.remedy.domain.achievement.AchievementType;
import org.example.remedy.domain.user.User;

public interface AchievementService {
    /**
     * 도전과제 생성 (관리자 전용)
     * @param title 도전과제 제목
     * @param type 도전과제 타입 (드로핑, 걷기, 노래 듣기)
     * @param period 도전과제 기간 (일일/상시)
     * @param targetValue 목표값
     * @param rewardAmount 보상 재화량
     * @param admin 관리자 사용자
     * @return 생성된 도전과제 정보
     */
    AchievementResponse createAchievement(String title, AchievementType type, AchievementPeriod period, Integer targetValue, Integer rewardAmount, User admin);

    /**
     * 활성화된 도전과제 목록을 사용자의 진행 상황과 함께 조회
     * @param user 조회할 사용자
     * @return 활성화된 도전과제 목록 및 사용자 진행 상황
     */
    UserAchievementListResponse getActiveAchievementsWithUserProgress(User user);

    /**
     * 활성화된 도전과제 목록을 기간별로 페이징 조회
     * @param user 조회할 사용자
     * @param period 도전과제 기간 (null이면 전체 조회)
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 페이징된 활성화된 도전과제 목록 및 사용자 진행 상황
     */
    PagedUserAchievementListResponse getActiveAchievementsWithUserProgressPaged(User user, AchievementPeriod period, int page, int size);

    AchievementResponse updateAchievement(Long achievementId, String title, Integer targetValue, Integer rewardAmount, User admin);
    
    void deactivateAchievement(Long achievementId, User admin);
    
    void activateAchievement(Long achievementId, User admin);

    UserAchievementResponse claimReward(Long achievementId, User user);
    
    void updateUserProgress(Long userId, AchievementType type, Integer progress);
}