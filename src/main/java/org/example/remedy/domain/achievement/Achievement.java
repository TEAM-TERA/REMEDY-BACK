package org.example.remedy.domain.achievement;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;


/**
 * 도전과제 엔티티
 * 사용자가 완료할 수 있는 다양한 도전과제의 정보를 관리
 */
@Getter
@NoArgsConstructor
@Table(name = "achievements")
@Entity
public class Achievement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long achievementId;

    @Column(nullable = false, length = 100)
    private String title; // 도전과제 이름


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AchievementType type; // 미션종류, 걷기, 드랍핑, 노래 듣기

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AchievementPeriod period; // 일일, 상시

    @Column(nullable = false)
    private Integer targetValue; // 달성 목표값

    @Column(nullable = false)
    private Integer rewardAmount; // 완료 시 재화 보상량

    @Column(length = 50)
    private String rewardTitle; // 완료 시 칭호 보상 (선택사항)

    @Column(columnDefinition = "TEXT")
    private String rewardTitleDescription; // 보상 칭호 설명

    @Column(nullable = false)
    private boolean isActive = true; // 활성화 여부


    @Column(nullable = false)
    private Long createdBy; // 생성자 (관리자) ID

    private Achievement(String title, AchievementType type, AchievementPeriod period, Integer targetValue, Integer rewardAmount, String rewardTitle, String rewardTitleDescription, Long createdBy) {
        this.title = title;
        this.type = type;
        this.period = period;
        this.targetValue = targetValue;
        this.rewardAmount = rewardAmount;
        this.rewardTitle = rewardTitle;
        this.rewardTitleDescription = rewardTitleDescription;
        this.createdBy = createdBy;
        this.isActive = true;
    }

    /**
     * 도전과제 생성
     * @param title 도전과제 제목
     * @param type 도전과제 유형 (드로핑, 걷기, 청취)
     * @param period 도전과제 기간 (일일, 상시)
     * @param targetValue 달성 목표값
     * @param rewardAmount 완료 시 재화 보상량
     * @param rewardTitle 완료 시 칭호 보상 (선택사항)
     * @param rewardTitleDescription 보상 칭호 설명
     * @param createdBy 생성자 (관리자) ID
     * @return 새로운 Achievement 인스턴스
     */
    public static Achievement create(String title, AchievementType type, AchievementPeriod period, Integer targetValue, Integer rewardAmount, String rewardTitle, String rewardTitleDescription, Long createdBy) {
        return new Achievement(title, type, period, targetValue, rewardAmount, rewardTitle, rewardTitleDescription, createdBy);
    }

    /**
     * 도전과제 비활성화
     * 비활성화된 도전과제는 사용자에게 표시되지 않음
     */
    public void deactivate() {
        this.isActive = false;
    }

    /**
     * 도전과제 활성화
     * 활성화된 도전과제만 사용자에게 표시됨
     */
    public void activate() {
        this.isActive = true;
    }

    /**
     * 도전과제 정보 업데이트
     * @param title 수정할 제목 (null이면 변경 안함)
     * @param targetValue 수정할 목표값 (null이면 변경 안함)
     * @param rewardAmount 수정할 보상량 (null이면 변경 안함)
     * @param rewardTitle 수정할 보상 칭호 (null이면 변경 안함)
     * @param rewardTitleDescription 수정할 보상 칭호 설명 (null이면 변경 안함)
     */
    public void updateInfo(String title, Integer targetValue, Integer rewardAmount, String rewardTitle, String rewardTitleDescription) {
        if (title != null && !title.isBlank()) {
            this.title = title;
        }
        if (targetValue != null && targetValue > 0) {
            this.targetValue = targetValue;
        }
        if (rewardAmount != null && rewardAmount >= 0) {
            this.rewardAmount = rewardAmount;
        }
        if (rewardTitle != null && !rewardTitle.isBlank()) {
            this.rewardTitle = rewardTitle;
        }
        if (rewardTitleDescription != null && !rewardTitleDescription.isBlank()) {
            this.rewardTitleDescription = rewardTitleDescription;
        }
    }

    /**
     * 보상 칭호 존재 여부 확인
     * @return 보상 칭호가 있으면 true, 없으면 false
     */
    public boolean hasRewardTitle() {
        return this.rewardTitle != null && !this.rewardTitle.isBlank();
    }

    /**
     * 일일 도전과제인지 확인
     * @return 일일 도전과제이면 true, 아니면 false
     */
    public boolean isDaily() {
        return this.period == AchievementPeriod.DAILY;
    }

    /**
     * 상시 도전과제인지 확인
     * @return 상시 도전과제이면 true, 아니면 false
     */
    public boolean isPermanent() {
        return this.period == AchievementPeriod.PERMANENT;
    }
}