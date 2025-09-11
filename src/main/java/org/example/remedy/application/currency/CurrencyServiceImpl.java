package org.example.remedy.application.currency;

import lombok.RequiredArgsConstructor;
import org.example.remedy.application.currency.dto.response.UserCurrencyResponse;
import org.example.remedy.application.currency.exception.InsufficientCurrencyException;
import org.example.remedy.application.currency.exception.UserCurrencyNotFoundException;
import org.example.remedy.application.currency.port.in.CurrencyService;
import org.example.remedy.application.currency.port.out.CurrencyPersistencePort;
import org.example.remedy.domain.currency.UserCurrency;
import org.example.remedy.domain.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 통화 서비스 구현체
 * 
 * 사용자의 통화 관리를 담당하는 서비스로,
 * 통화 조회, 획득, 소모 및 초기화 기능을 제공합니다.
 */
@Service
@RequiredArgsConstructor
public class CurrencyServiceImpl implements CurrencyService {
    private final CurrencyPersistencePort currencyPersistencePort;

    /**
     * 사용자 통화 정보 조회
     * 
     * @param user 조회할 사용자 정보
     * @return 사용자 통화 정보
     * @throws UserCurrencyNotFoundException 사용자 통화 정보가 존재하지 않는 경우
     */
    @Override
    @Transactional(readOnly = true)
    public UserCurrencyResponse getUserCurrency(User user) {
        UserCurrency userCurrency = currencyPersistencePort.findByUserId(user.getUserId())
                .orElseThrow(() -> UserCurrencyNotFoundException.INSTANCE);
        
        return UserCurrencyResponse.from(userCurrency);
    }

    /**
     * 사용자 통화 획득
     * 도전과제 보상, 이벤트 등으로 통화를 획득할 때 사용
     * 
     * @param user 통화를 획득할 사용자
     * @param amount 획득할 통화 양
     * @return 업데이트된 사용자 통화 정보
     */
    @Override
    @Transactional
    public UserCurrencyResponse earnCurrency(User user, Integer amount) {
        UserCurrency userCurrency = findOrCreateUserCurrency(user.getUserId());
        
        userCurrency.earn(amount);
        UserCurrency savedUserCurrency = currencyPersistencePort.save(userCurrency);
        
        return UserCurrencyResponse.from(savedUserCurrency);
    }

    /**
     * 사용자 통화 소모
     * 칭호 구매, 아이템 구매 등에서 통화를 소모할 때 사용
     * 
     * @param user 통화를 소모할 사용자
     * @param amount 소모할 통화 양
     * @return 업데이트된 사용자 통화 정보
     * @throws UserCurrencyNotFoundException 사용자 통화 정보가 존재하지 않는 경우
     * @throws InsufficientCurrencyException 통화가 부족한 경우
     */
    @Override
    @Transactional
    public UserCurrencyResponse spendCurrency(User user, Integer amount) {
        UserCurrency userCurrency = currencyPersistencePort.findByUserId(user.getUserId())
                .orElseThrow(() -> UserCurrencyNotFoundException.INSTANCE);
        
        if (!userCurrency.canSpend(amount)) {
            throw InsufficientCurrencyException.INSTANCE;
        }
        
        userCurrency.spend(amount);
        UserCurrency savedUserCurrency = currencyPersistencePort.save(userCurrency);
        
        return UserCurrencyResponse.from(savedUserCurrency);
    }

    /**
     * 사용자 통화 초기화
     * 사용자 가입 시 통화 데이터를 초기화합니다.
     * 이미 통화 데이터가 존재하는 경우는 무시합니다.
     * 
     * @param userId 초기화할 사용자 ID
     */
    @Override
    @Transactional
    public void initializeUserCurrency(Long userId) {
        if (!currencyPersistencePort.existsByUserId(userId)) {
            UserCurrency userCurrency = UserCurrency.create(userId);
            currencyPersistencePort.save(userCurrency);
        }
    }

    /**
     * 사용자 통화 조회 또는 생성
     * 기존 통화 데이터가 있으면 반환하고, 없으면 새로 생성합니다.
     * 
     * @param userId 사용자 ID
     * @return 사용자 통화 엔티티
     */
    private UserCurrency findOrCreateUserCurrency(Long userId) {
        return currencyPersistencePort.findByUserId(userId)
                .orElseGet(() -> {
                    UserCurrency userCurrency = UserCurrency.create(userId);
                    return currencyPersistencePort.save(userCurrency);
                });
    }
}