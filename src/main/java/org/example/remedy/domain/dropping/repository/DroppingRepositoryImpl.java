package org.example.remedy.domain.dropping.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.example.remedy.domain.dropping.application.exception.DroppingAlreadyExistsException;
import org.example.remedy.domain.dropping.domain.Dropping;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class DroppingRepositoryImpl implements DroppingRepository {

    private final DroppingPersistenceRepository repository;

    @PersistenceContext
    private EntityManager entityManager;

    private static final double DROPPING_CONSTRAINT_DISTANCE = 0.001;

    public void createDropping(Dropping dropping) {
        List<Dropping> nearbyDroppings = findDroppingsByAroundRadius(
                dropping.getLongitude(),
                dropping.getLatitude(),
                DROPPING_CONSTRAINT_DISTANCE
        );

        if (!nearbyDroppings.isEmpty()) {
            throw DroppingAlreadyExistsException.EXCEPTION;
        }

        repository.save(dropping);
    }

    @Override
    public List<Dropping> findActiveDroppingsWithinRadius(double longitude, double latitude, double distance) {
        return findDroppingsByAroundRadius(longitude, latitude, distance);
    }

    @Override
    public Optional<Dropping> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<Dropping> findByUserId(Long userId, Sort sort) {
        return repository.findByUserId(userId, sort);
    }

    @Override
    public List<Dropping> findExpiredAndNotDeletedDroppings(LocalDateTime now) {
        return repository.findExpiredAndNotDeletedDroppings(now);
    }

    @Override
    public void softDelete(Dropping dropping) {
        dropping.markAsDeleted();
        repository.save(dropping);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public void save(Dropping dropping) {
        repository.save(dropping);
    }

    @Override
    public void saveAll(List<Dropping> droppings) {
        repository.saveAll(droppings);
    }

    @Override
    public boolean existsById(Long id) {
        return repository.existsById(id);
    }

    /**
     * 하버사인 공식을 사용한 위치 기반 검색
     * PostgreSQL의 경우 PostGIS 확장을 사용하면 더 효율적이지만,
     * 기본 SQL로도 구현 가능
     */
    private List<Dropping> findDroppingsByAroundRadius(double longitude, double latitude, double distance) {
        // 대략적인 위도/경도 범위 계산 (1도 ≈ 111km)
        double latOffset = distance / 111.0;
        double lonOffset = distance / (111.0 * Math.cos(Math.toRadians(latitude)));

        double minLat = latitude - latOffset;
        double maxLat = latitude + latOffset;
        double minLon = longitude - lonOffset;
        double maxLon = longitude + lonOffset;

        // JPQL을 사용한 범위 검색
        String jpql = """
            SELECT d FROM Dropping d
            WHERE d.isDeleted = false
            AND d.expiryDate > :now
            AND d.latitude BETWEEN :minLat AND :maxLat
            AND d.longitude BETWEEN :minLon AND :maxLon
            """;

        return entityManager.createQuery(jpql, Dropping.class)
                .setParameter("now", LocalDateTime.now())
                .setParameter("minLat", minLat)
                .setParameter("maxLat", maxLat)
                .setParameter("minLon", minLon)
                .setParameter("maxLon", maxLon)
                .getResultList();
    }
}
