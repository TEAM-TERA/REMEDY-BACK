package org.example.remedy.domain.title;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 칭호 엔티티
 * 사용자가 구매하고 장착할 수 있는 칭호 정보를 관리
 */
@Getter
@NoArgsConstructor
@Table(name = "titles")
@Entity
public class Title {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long titleId; // 칭호 ID

    @Column(nullable = false, length = 50, unique = true)
    private String name; // 칭호 이름 (고유)

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description; // 칭호 설명

    @Column(nullable = false)
    private Integer price; // 칭호 가격 (재화로 구매)

    @Column(nullable = false)
    private boolean isActive = true; // 활성화 여부

    @Column(nullable = false)
    private LocalDateTime createdAt; // 생성 시간

    @Column(nullable = false)
    private Long createdBy; // 생성자 (관리자) ID

    private Title(String name, String description, Integer price, Long createdBy) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.createdBy = createdBy;
        this.createdAt = LocalDateTime.now();
        this.isActive = true;
    }

    /**
     * 칭호 생성
     * @param name 칭호 이름
     * @param description 칭호 설명
     * @param price 칭호 가격
     * @param createdBy 생성자 (관리자) ID
     * @return 새로운 Title 인스턴스
     */
    public static Title create(String name, String description, Integer price, Long createdBy) {
        return new Title(name, description, price, createdBy);
    }

    /**
     * 칭호 비활성화
     * 비활성화된 칭호는 구매할 수 없음
     */
    public void deactivate() {
        this.isActive = false;
    }

    /**
     * 칭호 활성화
     * 활성화된 칭호만 구매 가능
     */
    public void activate() {
        this.isActive = true;
    }

    /**
     * 칭호 정보 업데이트
     * @param name 수정할 칭호 이름 (null이면 변경 안함)
     * @param description 수정할 칭호 설명 (null이면 변경 안함)
     * @param price 수정할 칭호 가격 (null이면 변경 안함)
     */
    public void updateInfo(String name, String description, Integer price) {
        if (name != null && !name.isBlank()) {
            this.name = name;
        }
        if (description != null && !description.isBlank()) {
            this.description = description;
        }
        if (price != null && price >= 0) {
            this.price = price;
        }
    }
}