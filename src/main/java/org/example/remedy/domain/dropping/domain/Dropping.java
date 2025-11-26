package org.example.remedy.domain.dropping.domain;

import lombok.Getter;
import org.example.remedy.domain.dropping.application.exception.InvalidDroppingTypeException;
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

    private DroppingType droppingType;

    private Payload payload;

    private Long userId;

    private String content;

    private Double latitude;

    private Double longitude;

    private String address;

    private LocalDateTime expiryDate;

    private LocalDateTime createdAt;

    private boolean isDeleted = false;

    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint location;

    protected Dropping() {}

    public Dropping(DroppingType droppingType, Payload payload, Long userId, String content, Double latitude, Double longitude, String address, LocalDateTime expiryDate, LocalDateTime createdAt, boolean isDeleted) {
        this.droppingType = droppingType;
        this.payload = payload;
        this.userId = userId;
        this.content = content;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.expiryDate = expiryDate;
        this.createdAt = createdAt;
        this.isDeleted = isDeleted;
        this.location = new GeoJsonPoint(longitude, latitude);
    }

    private VoteDroppingPayload asVotePayload() {
        if (!(this.payload instanceof VoteDroppingPayload votePayload)) {
            throw InvalidDroppingTypeException.EXCEPTION;
        }
        return votePayload;
    }

    public VoteDroppingPayload getVotePayload() {
        return asVotePayload();
    }

    public void vote(Long userId, String optionText) {
        asVotePayload().addVote(userId, optionText);
    }

    public void cancelVote(Long userId) {
        asVotePayload().removeVote(userId);
    }

    public String getSongId() {
        if (this.payload instanceof MusicDroppingPayload musicPayload) {
            return musicPayload.getSongId();
        }
        return null;
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

	public boolean isMyDropping(Long userId) {
		return this.userId.equals(userId);
	}
}
