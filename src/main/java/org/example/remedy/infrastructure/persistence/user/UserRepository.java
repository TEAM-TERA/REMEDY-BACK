package org.example.remedy.infrastructure.persistence.user;

import org.example.remedy.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsUserByEmail(String email);
    Optional<User> findByUserId(Long userId);
}
