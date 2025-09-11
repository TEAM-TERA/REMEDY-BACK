package org.example.remedy.application.achievement.port.in;

import org.example.remedy.application.achievement.dto.response.AchievementListResponse;
import org.example.remedy.application.achievement.dto.response.AchievementResponse;
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
    
    AchievementListResponse getAllAchievements();
    
    AchievementListResponse getActiveAchievements();
    
    AchievementResponse updateAchievement(Long achievementId, String title, Integer targetValue, Integer rewardAmount, User admin);
    
    void deactivateAchievement(Long achievementId, User admin);
    
    void activateAchievement(Long achievementId, User admin);
    
    UserAchievementListResponse getUserAchievements(User user);
    
    UserAchievementResponse claimReward(Long achievementId, User user);
    
    void updateUserProgress(Long userId, AchievementType type, Integer progress);
}