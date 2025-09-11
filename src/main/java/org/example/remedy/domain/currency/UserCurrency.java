package org.example.remedy.domain.currency;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 사용자 재화 엔티티
 * 각 사용자가 보유한 게임 내 재화(포인트)를 관리
 */
@Getter
@NoArgsConstructor
@Table(name = "user_currency")
@Entity
public class UserCurrency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userCurrencyId; // 사용자 재화 ID

    @Column(nullable = false, unique = true)
    private Long userId; // 사용자 ID (unique)

    @Column(nullable = false)
    private Integer amount = 0; // 보유 재화 수량

    @Column(nullable = false)
    private LocalDateTime createdAt; // 생성 시간

    @Column(nullable = false)
    private LocalDateTime updatedAt; // 최종 업데이트 시간

    private UserCurrency(Long userId) {
        this.userId = userId;
        this.amount = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 사용자 재화 정보 생성
     * @param userId 사용자 ID
     * @return 새로운 UserCurrency 인스턴스 (초기 재화 0)
     */
    public static UserCurrency create(Long userId) {
        return new UserCurrency(userId);
    }

    /**
     * 재화 사용 가능 여부 확인
     * @param cost 사용할 재화 수량
     * @return 사용 가능하면 true, 아니면 false
     */
    public boolean canSpend(Integer cost) {
        return cost != null && cost >= 0 && this.amount >= cost;
    }

    /**
     * 재화 사용 (차감)
     * @param cost 사용할 재화 수량
     * @throws IllegalArgumentException 재화가 부족한 경우
     */
    public void spend(Integer cost) {
        if (!canSpend(cost)) {
            throw new IllegalArgumentException("재화가 부족합니다.");
        }
        this.amount -= cost;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 재화 획득 (증가)
     * @param reward 획득할 재화 수량
     * @throws IllegalArgumentException 보상 금액이 음수인 경우
     */
    public void earn(Integer reward) {
        if (reward == null || reward < 0) {
            throw new IllegalArgumentException("보상 금액은 0 이상이어야 합니다.");
        }
        this.amount += reward;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 재화 초기화 (0으로 설정)
     * 관리자 기능이나 테스트 용도로 사용
     */
    public void reset() {
        this.amount = 0;
        this.updatedAt = LocalDateTime.now();
    }
}