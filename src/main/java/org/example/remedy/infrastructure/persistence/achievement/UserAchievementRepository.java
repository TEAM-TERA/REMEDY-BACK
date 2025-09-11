package org.example.remedy.infrastructure.persistence.achievement;

import org.example.remedy.domain.achievement.UserAchievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserAchievementRepository extends JpaRepository<UserAchievement, Long> {
    Optional<UserAchievement> findByUserIdAndAchievementId(Long userId, Long achievementId);
    List<UserAchievement> findByUserId(Long userId);
    List<UserAchievement> findByUserIdAndIsCompletedTrue(Long userId);
    
    @Query("SELECT ua FROM UserAchievement ua JOIN Achievement a ON ua.achievementId = a.achievementId WHERE ua.userId = :userId AND a.type = :type")
    List<UserAchievement> findByUserIdAndType(@Param("userId") Long userId, @Param("type") String type);
}