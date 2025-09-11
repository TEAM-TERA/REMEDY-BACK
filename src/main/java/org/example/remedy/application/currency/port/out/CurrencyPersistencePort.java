package org.example.remedy.application.currency.port.out;

import org.example.remedy.domain.currency.UserCurrency;

import java.util.Optional;

public interface CurrencyPersistencePort {
    UserCurrency save(UserCurrency userCurrency);
    
    Optional<UserCurrency> findByUserId(Long userId);
    
    boolean existsByUserId(Long userId);
}