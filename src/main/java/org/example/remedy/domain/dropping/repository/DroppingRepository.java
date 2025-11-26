package org.example.remedy.domain.dropping.repository;

import org.example.remedy.domain.dropping.domain.Dropping;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DroppingRepository {

    void createDropping(Dropping dropping);
    List<Dropping> findActiveDroppingsWithinRadius(double longitude, double latitude, double distance);

    Optional<Dropping> findById(String id);
    List<Dropping> findByUserId(Long userId, Sort sort);
    List<Dropping> findExpiredAndNotDeletedDroppings(LocalDateTime now);

    void deleteById(String id);

    void save(Dropping dropping);

    void softDelete(Dropping dropping);

    void saveAll(List<Dropping> droppings);

    boolean existsById(String id);
}
