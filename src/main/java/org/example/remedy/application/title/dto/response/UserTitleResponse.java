package org.example.remedy.application.title.dto.response;

import org.example.remedy.domain.title.Title;
import org.example.remedy.domain.title.UserTitle;

import java.time.LocalDateTime;

/**
 * 사용자 칭호 정보 응답 DTO
 * 
 * 사용자가 보유한 칭호의 상세 정보를 제공합니다.
 * 
 * @param userTitleId 사용자 칭호 ID
 * @param titleId 칭호 ID
 * @param name 칭호 이름
 * @param description 칭호 설명
 * @param price 칭호 가격
 * @param isEquipped 착용 상태
 * @param purchasedAt 구매 시간
 * @param equippedAt 착용 시간
 */
public record UserTitleResponse(
        Long userTitleId,
        Long titleId,
        String name,
        String description,
        Integer price,
        boolean isEquipped,
        LocalDateTime purchasedAt,
        LocalDateTime equippedAt
) {
    /**
     * UserTitle 및 Title 엔티티로부터 응답 객체를 생성합니다.
     * 
     * @param userTitle 사용자 칭호 엔티티
     * @param title 칭호 엔티티
     * @return 사용자 칭호 정보 응답 DTO
     */
    public static UserTitleResponse from(UserTitle userTitle, Title title) {
        return new UserTitleResponse(
                userTitle.getUserTitleId(),
                title.getTitleId(),
                title.getName(),
                title.getDescription(),
                title.getPrice(),
                userTitle.isEquipped(),
                userTitle.getPurchasedAt(),
                userTitle.getEquippedAt()
        );
    }
}