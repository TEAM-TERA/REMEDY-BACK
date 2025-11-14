package org.example.remedy.infrastructure.persistence.dropping;

import lombok.RequiredArgsConstructor;
import org.example.remedy.application.dropping.exception.DroppingAlreadyExistsException;
import org.example.remedy.application.dropping.port.out.DroppingPersistencePort;
import org.example.remedy.domain.dropping.Dropping;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class MongoDroppingAdapter implements DroppingPersistencePort {

    private final DroppingRepository repository;
    private final MongoTemplate mongoTemplate;

    public void createDropping(Dropping dropping) {
        AggregationResults<Dropping> results = findDroppingsByAroundRadius(
                dropping.getLocation().getX(),
                dropping.getLocation().getY(),
                0.005);
        if (results.getMappedResults().isEmpty()){
            mongoTemplate.insert(dropping);
        } else {
            throw DroppingAlreadyExistsException.EXCEPTION;
        }
    }

    @Override
    public List<Dropping> findActiveDroppingsWithinRadius(double longitude, double latitude) {
        AggregationResults<Dropping> results = findDroppingsByAroundRadius(longitude, latitude, 0.1);
        return results.getMappedResults();
    }

    @Override
    public Optional<Dropping> findById(String id) {
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
    public void deleteById(String id) {
        repository.deleteById(id);
    }

    @Override
    public void saveAll(List<Dropping> droppings) {
        repository.saveAll(droppings);
    }

    @Override
    public boolean existsById(String id) {
        return repository.existsById(id);
    }

    private AggregationResults<Dropping> findDroppingsByAroundRadius(double longitude, double latitude, double distance) {
        Point location = new Point(longitude, latitude);
        Distance radius = new Distance(distance, Metrics.KILOMETERS);

        NearQuery nearQuery = NearQuery.near(location)
                .maxDistance(radius)
                .spherical(true)
                .query(Query.query(Criteria.where("isDeleted").is(false)
                        .and("expiryDate").gt(LocalDateTime.now())));

        Aggregation agg = Aggregation.newAggregation(
                Aggregation.geoNear(nearQuery, "distance"),
                Aggregation.project("userId", "songId", "content", "location", "distance", "latitude", "longitude", "address", "expiryDate", "createdAt", "isDeleted")
        );

        return mongoTemplate.aggregate(agg, "droppings", Dropping.class);
    }
}
