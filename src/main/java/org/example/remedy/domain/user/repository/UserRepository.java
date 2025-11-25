package org.example.remedy.domain.user.repository;

import org.example.remedy.domain.user.domain.OAuth2Provider;
import org.example.remedy.domain.user.domain.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findByEmail(String email);
    boolean existsUserByEmail(String email);
	Optional<User> findByProviderAndProviderId(OAuth2Provider provider, String providerId);
    Optional<User> findByUserId(Long userId);
    List<User> findUsersToDeletePermanently(LocalDateTime cutoffDate);
    void deleteAll(List<User> users);
}
