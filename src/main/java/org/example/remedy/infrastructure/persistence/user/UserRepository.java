package org.example.remedy.infrastructure.persistence.user;

import org.example.remedy.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsUserByEmail(String email);
    Optional<User> findByUserId(Long userId);
    @Query("SELECT u FROM User u WHERE u.status = 'WITHDRAWAL' AND u.withdrawalDate < :cutoffDate")
    List<User> findUsersToDeletePermanently(@Param("cutoffDate") LocalDateTime cutoffDate);

}
