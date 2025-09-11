package org.example.remedy.infrastructure.persistence.achievement;

import org.example.remedy.domain.achievement.Achievement;
import org.example.remedy.domain.achievement.AchievementType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AchievementRepository extends JpaRepository<Achievement, Long> {
    List<Achievement> findByIsActiveTrue();
    List<Achievement> findByType(AchievementType type);
    List<Achievement> findByTypeAndIsActiveTrue(AchievementType type);
}