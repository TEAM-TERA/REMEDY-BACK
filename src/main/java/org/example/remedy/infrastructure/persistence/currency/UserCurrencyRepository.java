package org.example.remedy.infrastructure.persistence.currency;

import org.example.remedy.domain.currency.UserCurrency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserCurrencyRepository extends JpaRepository<UserCurrency, Long> {
    Optional<UserCurrency> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
}