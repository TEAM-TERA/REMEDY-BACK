package org.example.remedy.application.title.dto.response;

import org.example.remedy.domain.title.Title;

import java.time.LocalDateTime;

/**
 * 칭호 정보 응답 DTO
 * 
 * 칭호의 기본 정보를 제공합니다.
 * 
 * @param titleId 칭호 ID
 * @param name 칭호 이름
 * @param description 칭호 설명
 * @param price 칭호 가격
 * @param isActive 활성화 상태
 * @param createdAt 생성 시간
 */
public record TitleResponse(
        Long titleId,
        String name,
        String description,
        Integer price,
        boolean isActive,
        LocalDateTime createdAt
) {
    /**
     * Title 엔티티로부터 응답 객체를 생성합니다.
     * 
     * @param title 칭호 엔티티
     * @return 칭호 정보 응답 DTO
     */
    public static TitleResponse from(Title title) {
        return new TitleResponse(
                title.getTitleId(),
                title.getName(),
                title.getDescription(),
                title.getPrice(),
                title.isActive(),
                title.getCreatedAt()
        );
    }
}