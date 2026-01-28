package org.example.remedy.domain.dropping.repository;

import org.example.remedy.domain.dropping.domain.Dropping;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DroppingPersistenceRepository extends JpaRepository<Dropping, Long> {
    List<Dropping> findByUserId(Long userId, Sort sort);

    @Query("SELECT d FROM Dropping d WHERE d.expiryDate < :now AND d.isDeleted = false")
    List<Dropping> findExpiredAndNotDeletedDroppings(@Param("now") LocalDateTime now);
}
