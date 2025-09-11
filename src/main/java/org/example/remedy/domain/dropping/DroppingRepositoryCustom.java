package org.example.remedy.domain.dropping;

import lombok.RequiredArgsConstructor;
import org.example.remedy.application.dropping.exception.DroppingAlreadyExistsException;
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

@RequiredArgsConstructor
@Repository
public class DroppingRepositoryCustom implements CreateDropping, FindActiveDroppings {
    private final MongoTemplate mongoTemplate;

    @Override
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
