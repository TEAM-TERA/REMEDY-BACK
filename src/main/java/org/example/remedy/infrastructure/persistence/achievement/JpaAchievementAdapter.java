package org.example.remedy.infrastructure.persistence.achievement;

import lombok.RequiredArgsConstructor;
import org.example.remedy.application.achievement.port.out.AchievementPersistencePort;
import org.example.remedy.domain.achievement.Achievement;
import org.example.remedy.domain.achievement.AchievementType;
import org.example.remedy.domain.achievement.UserAchievement;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JpaAchievementAdapter implements AchievementPersistencePort {
    private final AchievementRepository achievementRepository;
    private final UserAchievementRepository userAchievementRepository;

    @Override
    public Achievement save(Achievement achievement) {
        return achievementRepository.save(achievement);
    }

    @Override
    public Optional<Achievement> findById(Long achievementId) {
        return achievementRepository.findById(achievementId);
    }

    @Override
    public List<Achievement> findAll() {
        return achievementRepository.findAll();
    }

    @Override
    public List<Achievement> findByIsActiveTrue() {
        return achievementRepository.findByIsActiveTrue();
    }

    @Override
    public List<Achievement> findByType(AchievementType type) {
        return achievementRepository.findByType(type);
    }

    @Override
    public List<Achievement> findByTypeAndIsActiveTrue(AchievementType type) {
        return achievementRepository.findByTypeAndIsActiveTrue(type);
    }

    @Override
    public UserAchievement save(UserAchievement userAchievement) {
        return userAchievementRepository.save(userAchievement);
    }

    @Override
    public Optional<UserAchievement> findByUserIdAndAchievementId(Long userId, Long achievementId) {
        return userAchievementRepository.findByUserIdAndAchievementId(userId, achievementId);
    }

    @Override
    public List<UserAchievement> findByUserId(Long userId) {
        return userAchievementRepository.findByUserId(userId);
    }

    @Override
    public List<UserAchievement> findByUserIdAndIsCompletedTrue(Long userId) {
        return userAchievementRepository.findByUserIdAndIsCompletedTrue(userId);
    }

    @Override
    public List<UserAchievement> findByUserIdAndType(Long userId, AchievementType type) {
        return userAchievementRepository.findByUserIdAndType(userId, type.name());
    }
}