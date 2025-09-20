package org.example.remedy.application.dropping.port.out;

import org.example.remedy.domain.dropping.Dropping;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DroppingPersistencePort {

    void createDropping(Dropping dropping);
    List<Dropping> findActiveDroppingsWithinRadius(double longitude, double latitude);

    Optional<Dropping> findById(String id);
    List<Dropping> findByUserId(Long userId, Sort sort);
    List<Dropping> findExpiredAndNotDeletedDroppings(LocalDateTime now);

    void deleteById(String id);
    void saveAll(List<Dropping> droppings);
    boolean existsById(String id);
}
