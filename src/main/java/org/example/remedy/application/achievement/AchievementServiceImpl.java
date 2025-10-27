package org.example.remedy.application.achievement;

import lombok.RequiredArgsConstructor;
import org.example.remedy.application.achievement.dto.response.AchievementListResponse;
import org.example.remedy.application.achievement.dto.response.AchievementResponse;
import org.example.remedy.application.achievement.dto.response.PagedUserAchievementListResponse;
import org.example.remedy.application.achievement.dto.response.UserAchievementListResponse;
import org.example.remedy.application.achievement.dto.response.UserAchievementResponse;
import org.example.remedy.application.achievement.exception.AchievementNotCompletedException;
import org.example.remedy.application.achievement.exception.AchievementNotFoundException;
import org.example.remedy.application.achievement.exception.RewardAlreadyClaimedException;
import org.example.remedy.application.achievement.exception.UserAchievementNotFoundException;
import org.example.remedy.application.achievement.port.in.AchievementService;
import org.example.remedy.application.achievement.port.out.AchievementPersistencePort;
import org.example.remedy.application.currency.port.in.CurrencyService;
import org.example.remedy.domain.achievement.Achievement;
import org.example.remedy.domain.achievement.AchievementPeriod;
import org.example.remedy.domain.achievement.AchievementType;
import org.example.remedy.domain.achievement.UserAchievement;
import org.example.remedy.domain.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AchievementServiceImpl implements AchievementService {
    private final AchievementPersistencePort achievementPersistencePort;
    private final CurrencyService currencyService;

    /**
     * 도전과제 생성
     * SecurityConfig에서 관리자 권한 검증이 이미 처리됨
     */
    @Override
    @Transactional
    public AchievementResponse createAchievement(String title, AchievementType type, AchievementPeriod period, Integer targetValue, Integer rewardAmount, User admin) {
        // 도전과제 엔티티 생성 (기간 정보 포함)
        Achievement achievement = Achievement.create(title, type, period, targetValue, rewardAmount, null, null, admin.getUserId());
        Achievement savedAchievement = achievementPersistencePort.save(achievement);
        
        return AchievementResponse.from(savedAchievement);
    }

    /**
     * 활성화된 도전과제를 사용자의 진행 상황과 함께 조회
     * 활성화된 모든 도전과제와 각 도전과제에 대한 사용자의 진행 상황을 반환
     * 사용자가 아직 시작하지 않은 도전과제도 포함됨 (진행도 0으로 표시)
     *
     * @param user 사용자 정보
     * @return 활성화된 도전과제 목록 및 사용자 진행 상황
     */
    @Override
    @Transactional(readOnly = true)
    public UserAchievementListResponse getActiveAchievementsWithUserProgress(User user) {
        // 활성화된 모든 도전과제 조회
        List<Achievement> activeAchievements = achievementPersistencePort.findByIsActiveTrue();

        // 각 활성화된 도전과제에 대해 사용자의 진행 상황을 조회하거나 기본값 생성
        List<UserAchievementResponse> responses = activeAchievements.stream()
                .map(achievement -> {
                    UserAchievement userAchievement = achievementPersistencePort
                            .findByUserIdAndAchievementId(user.getUserId(), achievement.getAchievementId())
                            .orElse(UserAchievement.create(user.getUserId(), achievement.getAchievementId()));

                    return UserAchievementResponse.from(userAchievement, achievement);
                })
                .toList();

        return UserAchievementListResponse.from(responses);
    }

    /**
     * 도전과제 정보 수정
     * 관리자만 수정 가능
     * 
     * @param achievementId 수정할 도전과제 ID
     * @param title 새로운 제목
     * @param targetValue 새로운 목표값
     * @param rewardAmount 새로운 보상 금액
     * @param admin 관리자 정보
     * @return 수정된 도전과제 정보
     * @throws AchievementNotFoundException 도전과제가 존재하지 않는 경우
     */
    @Override
    @Transactional
    public AchievementResponse updateAchievement(Long achievementId, String title, Integer targetValue, Integer rewardAmount, User admin) {
        Achievement achievement = achievementPersistencePort.findById(achievementId)
                .orElseThrow(() -> AchievementNotFoundException.INSTANCE);
        
        achievement.updateInfo(title, targetValue, rewardAmount, null, null);
        Achievement savedAchievement = achievementPersistencePort.save(achievement);
        
        return AchievementResponse.from(savedAchievement);
    }

    /**
     * 도전과제 비활성화
     * 관리자만 비활성화 가능
     * 
     * @param achievementId 비활성화할 도전과제 ID
     * @param admin 관리자 정보
     * @throws AchievementNotFoundException 도전과제가 존재하지 않는 경우
     */
    @Override
    @Transactional
    public void deactivateAchievement(Long achievementId, User admin) {
        Achievement achievement = achievementPersistencePort.findById(achievementId)
                .orElseThrow(() -> AchievementNotFoundException.INSTANCE);
        
        achievement.deactivate();
        achievementPersistencePort.save(achievement);
    }

    /**
     * 도전과제 활성화
     * 관리자만 활성화 가능
     * 
     * @param achievementId 활성화할 도전과제 ID
     * @param admin 관리자 정보
     * @throws AchievementNotFoundException 도전과제가 존재하지 않는 경우
     */
    @Override
    @Transactional
    public void activateAchievement(Long achievementId, User admin) {
        Achievement achievement = achievementPersistencePort.findById(achievementId)
                .orElseThrow(() -> AchievementNotFoundException.INSTANCE);
        
        achievement.activate();
        achievementPersistencePort.save(achievement);
    }


    /**
     * 도전과제 보상 수령
     * 완료된 도전과제의 보상을 수령하고 통화를 지급
     * 
     * @param achievementId 보상을 수령할 도전과제 ID
     * @param user 사용자 정보
     * @return 보상 수령 후 사용자 도전과제 정보
     * @throws UserAchievementNotFoundException 사용자 도전과제가 존재하지 않는 경우
     * @throws AchievementNotCompletedException 도전과제가 아직 완료되지 않은 경우
     * @throws RewardAlreadyClaimedException 이미 보상을 수령한 경우
     * @throws AchievementNotFoundException 도전과제가 존재하지 않는 경우
     */
    @Override
    @Transactional
    public UserAchievementResponse claimReward(Long achievementId, User user) {
        UserAchievement userAchievement = achievementPersistencePort.findByUserIdAndAchievementId(user.getUserId(), achievementId)
                .orElseThrow(() -> UserAchievementNotFoundException.INSTANCE);
        
        if (!userAchievement.isCompleted()) {
            throw AchievementNotCompletedException.INSTANCE;
        }
        
        if (userAchievement.isRewardClaimed()) {
            throw RewardAlreadyClaimedException.INSTANCE;
        }
        
        Achievement achievement = achievementPersistencePort.findById(achievementId)
                .orElseThrow(() -> AchievementNotFoundException.INSTANCE);
        
        userAchievement.claimReward();
        achievementPersistencePort.save(userAchievement);
        
        currencyService.earnCurrency(user, achievement.getRewardAmount());
        
        return UserAchievementResponse.from(userAchievement, achievement);
    }

    /**
     * 활성화된 도전과제를 기간별로 페이징하여 사용자의 진행 상황과 함께 조회
     *
     * @param user 사용자 정보
     * @param period 도전과제 기간 (null이면 전체 조회)
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 페이징된 활성화된 도전과제 목록 및 사용자 진행 상황
     */
    @Override
    @Transactional(readOnly = true)
    public PagedUserAchievementListResponse getActiveAchievementsWithUserProgressPaged(User user, AchievementPeriod period, int page, int size) {
        // 활성화된 도전과제를 기간별로 페이징 조회
        List<Achievement> activeAchievements = achievementPersistencePort.findByIsActiveTrueAndPeriod(period, page, size);
        long totalCount = achievementPersistencePort.countByIsActiveTrueAndPeriod(period);

        // 각 활성화된 도전과제에 대해 사용자의 진행 상황을 조회하거나 기본값 생성
        List<UserAchievementResponse> responses = activeAchievements.stream()
                .map(achievement -> {
                    UserAchievement userAchievement = achievementPersistencePort
                            .findByUserIdAndAchievementId(user.getUserId(), achievement.getAchievementId())
                            .orElse(UserAchievement.create(user.getUserId(), achievement.getAchievementId()));

                    return UserAchievementResponse.from(userAchievement, achievement);
                })
                .toList();

        return PagedUserAchievementListResponse.of(responses, totalCount, page, size);
    }

    /**
     * 사용자 도전과제 진행 상황 업데이트
     * 특정 타입의 도전과제에 대한 사용자의 진행 상황을 업데이트
     *
     * @param userId 사용자 ID
     * @param type 도전과제 타입
     * @param progress 진행 상황 값
     */
    @Override
    @Transactional
    public void updateUserProgress(Long userId, AchievementType type, Integer progress) {
        List<Achievement> activeAchievements = achievementPersistencePort.findByTypeAndIsActiveTrue(type);

        for (Achievement achievement : activeAchievements) {
            UserAchievement userAchievement = achievementPersistencePort.findByUserIdAndAchievementId(userId, achievement.getAchievementId())
                    .orElse(UserAchievement.create(userId, achievement.getAchievementId()));

            userAchievement.updateProgress(progress, achievement.getTargetValue());
            achievementPersistencePort.save(userAchievement);
        }
    }

}