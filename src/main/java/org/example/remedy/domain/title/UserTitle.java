package org.example.remedy.domain.title;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 사용자가 보유한 칭호 엔티티
 * 사용자가 구매한 칭호와 장착 상태를 관리
 */
@Getter
@NoArgsConstructor
@Table(name = "user_titles",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "title_id"}))
@Entity
public class UserTitle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userTitleId; // 사용자 칭호 ID

    @Column(name = "user_id", nullable = false)
    private Long userId; // 사용자 ID

    @Column(name = "title_id", nullable = false)
    private Long titleId; // 칭호 ID

    @Column(nullable = false)
    private boolean isEquipped = false; // 장착 여부

    @Column(nullable = false)
    private LocalDateTime purchasedAt; // 구매 시간

    private LocalDateTime equippedAt; // 장착 시간

    private UserTitle(Long userId, Long titleId) {
        this.userId = userId;
        this.titleId = titleId;
        this.isEquipped = false;
        this.purchasedAt = LocalDateTime.now();
    }

    /**
     * 사용자 칭호 생성 (구매 시)
     * @param userId 사용자 ID
     * @param titleId 칭호 ID
     * @return 새로운 UserTitle 인스턴스
     */
    public static UserTitle create(Long userId, Long titleId) {
        return new UserTitle(userId, titleId);
    }

    /**
     * 칭호 장착
     * 한 번에 하나의 칭호만 장착 가능하므로, 다른 칭호는 해제 후 장착해야 함
     */
    public void equip() {
        this.isEquipped = true;
        this.equippedAt = LocalDateTime.now();
    }

    /**
     * 칭호 장착 해제
     */
    public void unequip() {
        this.isEquipped = false;
        this.equippedAt = null;
    }
}