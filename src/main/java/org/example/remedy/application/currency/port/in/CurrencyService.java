package org.example.remedy.application.currency.port.in;

import org.example.remedy.application.currency.dto.response.UserCurrencyResponse;
import org.example.remedy.domain.user.User;

/**
 * 재화 서비스 인터페이스
 * 사용자 재화 조회, 획득, 사용 기능을 제공
 */
public interface CurrencyService {
    /**
     * 사용자 재화 정보 조회
     * @param user 사용자
     * @return 사용자 재화 정보
     */
    UserCurrencyResponse getUserCurrency(User user);
    
    /**
     * 재화 획득 처리
     * @param user 사용자
     * @param amount 획득할 재화 수량
     * @return 업데이트된 사용자 재화 정보
     */
    UserCurrencyResponse earnCurrency(User user, Integer amount);
    
    /**
     * 재화 사용 처리
     * @param user 사용자
     * @param amount 사용할 재화 수량
     * @return 업데이트된 사용자 재화 정보
     * @throws org.example.remedy.application.currency.exception.InsufficientCurrencyException 재화가 부족한 경우
     */
    UserCurrencyResponse spendCurrency(User user, Integer amount);
    
    /**
     * 사용자 재화 정보 초기화
     * 신규 사용자 가입 시 호출
     * @param userId 사용자 ID
     */
    void initializeUserCurrency(Long userId);
}