package org.example.remedy.domain.achievement;

/**
 * 도전과제 유형
 * 사용자가 완료할 수 있는 다양한 활동 유형을 정의
 */
public enum AchievementType {
    DROPPING_COUNT("드로핑 횟수"),      // 음악 드로핑 생성 횟수
    WALKING_DISTANCE("걸은 거리(km)"), // 걸은 거리(킬로미터)
    LISTENING_COUNT("노래 청취 횟수");   // 노래 청취 횟수

    private final String description;

    AchievementType(String description) {
        this.description = description;
    }

    /**
     * 도전과제 유형의 한국어 설명을 반환
     * @return 유형 설명
     */
    public String getDescription() {
        return description;
    }
}