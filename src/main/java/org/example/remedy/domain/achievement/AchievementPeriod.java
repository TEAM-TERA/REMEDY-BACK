package org.example.remedy.domain.achievement;

/**
 * 도전과제 기간 타입
 * 일일 도전과제와 상시 도전과제를 구분하기 위한 enum
 */
public enum AchievementPeriod {
    DAILY("일일"),      // 매일 초기화되는 도전과제
    PERMANENT("상시");  // 누적되는 도전과제

    private final String description;

    AchievementPeriod(String description) {
        this.description = description;
    }

    /**
     * 기간 타입의 한국어 설명을 반환
     * @return 기간 타입 설명
     */
    public String getDescription() {
        return description;
    }
}