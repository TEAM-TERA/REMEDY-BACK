package org.example.remedy.infrastructure.persistence.currency;

import lombok.RequiredArgsConstructor;
import org.example.remedy.application.currency.port.out.CurrencyPersistencePort;
import org.example.remedy.domain.currency.UserCurrency;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JpaCurrencyAdapter implements CurrencyPersistencePort {
    private final UserCurrencyRepository userCurrencyRepository;

    @Override
    public UserCurrency save(UserCurrency userCurrency) {
        return userCurrencyRepository.save(userCurrency);
    }

    @Override
    public Optional<UserCurrency> findByUserId(Long userId) {
        return userCurrencyRepository.findByUserId(userId);
    }

    @Override
    public boolean existsByUserId(Long userId) {
        return userCurrencyRepository.existsByUserId(userId);
    }
}