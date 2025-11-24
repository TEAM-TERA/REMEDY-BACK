package org.example.remedy.domain.user.repository;

import lombok.RequiredArgsConstructor;
import org.example.remedy.domain.user.domain.OAuth2Provider;
import org.example.remedy.domain.user.domain.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserPersistenceRepository userPersistenceRepository;

    @Override
    public User save(User user) {
        return userPersistenceRepository.save(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userPersistenceRepository.findByEmail(email);
    }

    @Override
    public boolean existsUserByEmail(String email) {
        return userPersistenceRepository.existsUserByEmail(email);
    }

    @Override
    public Optional<User> findByUserId(Long userId) {
        return userPersistenceRepository.findByUserId(userId);
    }

    @Override
    public Optional<User> findByProviderAndProviderId(OAuth2Provider provider, String providerId) {
        return userPersistenceRepository.findByProviderAndProviderId(provider, providerId);
    }

    @Override
    public List<User> findUsersToDeletePermanently(LocalDateTime cutoffDate) {
        return userPersistenceRepository.findUsersToDeletePermanently(cutoffDate);
    }

    @Override
    public void deleteAll(List<User> users) {
        userPersistenceRepository.deleteAll(users);
    }

}
