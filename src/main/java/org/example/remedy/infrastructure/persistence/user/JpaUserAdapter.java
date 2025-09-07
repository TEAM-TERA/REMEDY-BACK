package org.example.remedy.infrastructure.persistence.user;

import lombok.RequiredArgsConstructor;
import org.example.remedy.application.user.port.out.UserPersistencePort;
import org.example.remedy.domain.user.User;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JpaUserAdapter implements UserPersistencePort {

    private final UserRepository userRepository;

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public boolean existsUserByEmail(String email) {
        return userRepository.existsUserByEmail(email);
    }
}
