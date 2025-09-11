package org.example.remedy.application.achievement.port.out;

import org.example.remedy.domain.achievement.Achievement;
import org.example.remedy.domain.achievement.AchievementType;
import org.example.remedy.domain.achievement.UserAchievement;

import java.util.List;
import java.util.Optional;

public interface AchievementPersistencePort {
    Achievement save(Achievement achievement);
    
    Optional<Achievement> findById(Long achievementId);
    
    List<Achievement> findAll();
    
    List<Achievement> findByIsActiveTrue();
    
    List<Achievement> findByType(AchievementType type);
    
    List<Achievement> findByTypeAndIsActiveTrue(AchievementType type);
    
    UserAchievement save(UserAchievement userAchievement);
    
    Optional<UserAchievement> findByUserIdAndAchievementId(Long userId, Long achievementId);
    
    List<UserAchievement> findByUserId(Long userId);
    
    List<UserAchievement> findByUserIdAndIsCompletedTrue(Long userId);
    
    List<UserAchievement> findByUserIdAndType(Long userId, AchievementType type);
}