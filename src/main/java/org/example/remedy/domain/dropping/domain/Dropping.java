package org.example.remedy.domain.dropping.domain;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.remedy.domain.dropping.application.exception.InvalidDroppingTypeException;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;

@Entity
@Table(name = "droppings")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Dropping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dropping_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "dropping_type", nullable = false)
    private DroppingType droppingType;

    @Type(JsonBinaryType.class)
    @Column(name = "payload", columnDefinition = "jsonb")
    private Payload payload;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    private String content;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private boolean isDeleted = false;

    public Dropping(DroppingType droppingType, Payload payload, Long userId,
                    String content, Double latitude, Double longitude,
                    String address, LocalDateTime expiryDate, LocalDateTime createdAt, boolean isDeleted) {
        this.droppingType = droppingType;
        this.payload = payload;
        this.userId = userId;
        this.content = content;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.expiryDate = expiryDate;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.isDeleted = isDeleted;
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
