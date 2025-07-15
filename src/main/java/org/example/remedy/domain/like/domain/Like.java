package org.example.remedy.domain.like.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Document(collection = "likes")
public class Like {

    @Id
    private String id;

    private Long userId;

    private String targetId;

    private TargetType targetType;

    private LocalDateTime createdAt;

    private Like(Long userId, String targetId, TargetType targetType, LocalDateTime createdAt) {
        this.userId = userId;
        this.targetId = targetId;
        this.targetType = targetType;
        this.createdAt = createdAt;
    }

    public static Like getInstance(Long userId, String targetId, TargetType targetType) {
        return new Like(userId, targetId, targetType, LocalDateTime.now());
    }
}
