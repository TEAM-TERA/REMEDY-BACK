package org.example.remedy.application.achievement.port.out;

import org.example.remedy.domain.achievement.Achievement;
import org.example.remedy.domain.achievement.AchievementPeriod;
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

    /**
     * 활성화된 도전과제를 기간별로 페이징 조회
     * @param period 도전과제 기간 (null이면 전체 조회)
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 페이징된 활성화된 도전과제 목록
     */
    List<Achievement> findByIsActiveTrueAndPeriod(AchievementPeriod period, int page, int size);

    /**
     * 활성화된 도전과제의 총 개수를 기간별로 조회
     * @param period 도전과제 기간 (null이면 전체 조회)
     * @return 활성화된 도전과제 총 개수
     */
    long countByIsActiveTrueAndPeriod(AchievementPeriod period);

    UserAchievement save(UserAchievement userAchievement);

    Optional<UserAchievement> findByUserIdAndAchievementId(Long userId, Long achievementId);

    List<UserAchievement> findByUserId(Long userId);

    List<UserAchievement> findByUserIdAndIsCompletedTrue(Long userId);

    List<UserAchievement> findByUserIdAndType(Long userId, AchievementType type);
}