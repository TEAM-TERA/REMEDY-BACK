package org.example.remedy.application.user.port.out;

import org.example.remedy.domain.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserPersistencePort  {
    void save(User user);
    Optional<User> findByEmail(String email);
    boolean existsUserByEmail(String email);
    Optional<User> findByUserId(Long userId);
    List<User> findUsersToDeletePermanently(LocalDateTime cutoffDate);
    void deleteAll(List<User> users);

}
