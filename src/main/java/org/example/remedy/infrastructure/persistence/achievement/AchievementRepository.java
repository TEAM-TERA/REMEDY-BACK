package org.example.remedy.infrastructure.persistence.achievement;

import org.example.remedy.domain.achievement.Achievement;
import org.example.remedy.domain.achievement.AchievementPeriod;
import org.example.remedy.domain.achievement.AchievementType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AchievementRepository extends JpaRepository<Achievement, Long> {
    List<Achievement> findByIsActiveTrue();
    List<Achievement> findByType(AchievementType type);
    List<Achievement> findByTypeAndIsActiveTrue(AchievementType type);

    Page<Achievement> findByIsActiveTrueAndPeriod(AchievementPeriod period, Pageable pageable);
    Page<Achievement> findByIsActiveTrue(Pageable pageable);

    long countByIsActiveTrueAndPeriod(AchievementPeriod period);
    long countByIsActiveTrue();
}