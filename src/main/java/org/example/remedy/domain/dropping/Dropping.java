package org.example.remedy.domain.dropping;

import lombok.Getter;
import org.example.remedy.interfaces.dropping.dto.request.DroppingCreateRequest;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Document(collection = "droppings")
public class Dropping {
    @Id
    private String droppingId;
    
    private Long userId;
    
    private String songId;
    
    private String content;
    
    private Double latitude;
    
    private Double longitude;
    
    private String address;

    private LocalDateTime expiryDate;
    
    private LocalDateTime createdAt;
    
    private boolean isDeleted = false;

    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint location;

    private Dropping() {}

    public Dropping(Long userId, String songId, String content, Double latitude, Double longitude, String address, LocalDateTime expiryDate, LocalDateTime createdAt, boolean isDeleted) {
        this.userId = userId;
        this.songId = songId;
        this.content = content;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.expiryDate = expiryDate;
        this.createdAt = createdAt;
        this.isDeleted = isDeleted;
        this.location = new GeoJsonPoint(longitude, latitude);
    }

    public static Dropping getInstance(Long userId, DroppingCreateRequest request) {
        LocalDateTime now = LocalDateTime.now();

        return new Dropping(
                userId,
                request.songId(),
                request.content(),
                request.latitude(),
                request.longitude(),
                request.address(),
                now.plusDays(3),
                now,
                false
        );
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiryDate);
    }
    
    public void markAsDeleted() {
        this.isDeleted = true;
    }

    public boolean isActive() {
        return !isDeleted && !isExpired();
    }
}
