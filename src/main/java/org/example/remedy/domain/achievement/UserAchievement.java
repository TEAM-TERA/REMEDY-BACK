package org.example.remedy.domain.achievement;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 사용자별 도전과제 진행 상황
 * 각 사용자가 도전과제를 얼마나 진행했는지, 완료했는지, 보상을 받았는지 추적
 */
@Getter
@NoArgsConstructor
@Table(name = "user_achievements", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "achievement_id"}))
@Entity
public class UserAchievement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userAchievementId;

    @Column(name = "user_id", nullable = false)
    private Long userId; // 사용자 ID

    @Column(name = "achievement_id", nullable = false)
    private Long achievementId; // 도전과제 ID

    @Column(nullable = false)
    private Integer currentProgress = 0; // 현재 진행도

    @Column(nullable = false)
    private boolean isCompleted = false; // 완료 여부

    @Column(nullable = false)
    private boolean isRewardClaimed = false; // 보상 수령 여부

    @Column(nullable = false)
    private LocalDateTime createdAt; // 생성 시간

    private LocalDateTime completedAt; // 완료 시간

    private LocalDateTime rewardClaimedAt; // 보상 수령 시간

    private UserAchievement(Long userId, Long achievementId) {
        this.userId = userId;
        this.achievementId = achievementId;
        this.currentProgress = 0;
        this.isCompleted = false;
        this.isRewardClaimed = false;
        this.createdAt = LocalDateTime.now();
    }

    /**
     * 사용자 도전과제 진행 상황 생성
     * @param userId 사용자 ID
     * @param achievementId 도전과제 ID
     * @return 새로운 UserAchievement 인스턴스
     */
    public static UserAchievement create(Long userId, Long achievementId) {
        return new UserAchievement(userId, achievementId);
    }

    /**
     * 진행도를 특정 값으로 업데이트 (절대값 설정)
     * @param progress 설정할 진행도 값
     * @param targetValue 목표값
     */
    public void updateProgress(Integer progress, Integer targetValue) {
        if (isCompleted) {
            return;
        }

        this.currentProgress = Math.max(0, progress);
        
        if (this.currentProgress >= targetValue) {
            complete();
        }
    }

    /**
     * 진행도를 추가 (상대값 증가)
     * @param additionalProgress 추가할 진행도
     * @param targetValue 목표값
     */
    public void addProgress(Integer additionalProgress, Integer targetValue) {
        if (isCompleted) {
            return;
        }

        this.currentProgress += Math.max(0, additionalProgress);
        
        if (this.currentProgress >= targetValue) {
            complete();
        }
    }

    /**
     * 도전과제 완료 처리
     */
    private void complete() {
        this.isCompleted = true;
        this.completedAt = LocalDateTime.now();
    }

    /**
     * 보상 수령 처리
     * 완료된 도전과제만 보상을 받을 수 있음
     */
    public void claimReward() {
        if (!isCompleted || isRewardClaimed) {
            return;
        }

        this.isRewardClaimed = true;
        this.rewardClaimedAt = LocalDateTime.now();
    }

    /**
     * 진행률을 백분율로 계산
     * @param targetValue 목표값
     * @return 진행률 (0.0 ~ 100.0)
     */
    public double getProgressPercentage(Integer targetValue) {
        if (targetValue == null || targetValue == 0) {
            return 0.0;
        }
        return Math.min(100.0, (double) currentProgress / targetValue * 100.0);
    }
}