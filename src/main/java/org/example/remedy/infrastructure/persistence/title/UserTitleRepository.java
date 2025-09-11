package org.example.remedy.infrastructure.persistence.title;

import org.example.remedy.domain.title.UserTitle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserTitleRepository extends JpaRepository<UserTitle, Long> {
    Optional<UserTitle> findByUserIdAndTitleId(Long userId, Long titleId);
    List<UserTitle> findByUserId(Long userId);
    Optional<UserTitle> findByUserIdAndIsEquippedTrue(Long userId);
    
    @Modifying
    @Query("UPDATE UserTitle ut SET ut.isEquipped = false WHERE ut.userId = :userId AND ut.isEquipped = true")
    void unequipAllTitles(@Param("userId") Long userId);
}