package org.example.remedy.application.currency.dto.response;

import org.example.remedy.domain.currency.UserCurrency;

import java.time.LocalDateTime;

/**
 * 사용자 통화 정보 응답 DTO
 * 
 * 사용자가 보유한 통화 정보를 제공합니다.
 * 
 * @param userCurrencyId 사용자 통화 ID
 * @param userId 사용자 ID
 * @param amount 보유 통화 양
 * @param updatedAt 마지막 업데이트 시간
 */
public record UserCurrencyResponse(
        Long userCurrencyId,
        Long userId,
        Integer amount,
        LocalDateTime updatedAt
) {
    /**
     * UserCurrency 엔티티로부터 응답 객체를 생성합니다.
     * 
     * @param userCurrency 사용자 통화 엔티티
     * @return 사용자 통화 정보 응답 DTO
     */
    public static UserCurrencyResponse from(UserCurrency userCurrency) {
        return new UserCurrencyResponse(
                userCurrency.getUserCurrencyId(),
                userCurrency.getUserId(),
                userCurrency.getAmount(),
                userCurrency.getUpdatedAt()
        );
    }
}