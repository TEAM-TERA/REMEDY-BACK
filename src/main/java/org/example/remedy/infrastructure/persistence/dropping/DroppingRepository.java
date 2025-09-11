package org.example.remedy.infrastructure.persistence.dropping;

import org.example.remedy.domain.dropping.Dropping;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DroppingRepository extends MongoRepository<Dropping, String> {
    List<Dropping> findByUserId(Long userId, Sort sort);

    @Query("{ 'expiryDate': { '$lt': ?0 }, 'isDeleted': false }")
    List<Dropping> findExpiredAndNotDeletedDroppings(LocalDateTime now);
}
