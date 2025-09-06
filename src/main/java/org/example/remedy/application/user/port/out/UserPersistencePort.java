package org.example.remedy.application.user.port.out;

import org.example.remedy.domain.user.User;

import java.util.Optional;

public interface UserPersistencePort  {
    void save(User user);
    Optional<User> findByEmail(String email);
    boolean existsUserByEmail(String email);
}
