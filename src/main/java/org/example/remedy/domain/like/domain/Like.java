package org.example.remedy.domain.like.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "likes")
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String targetId;

    @Enumerated(EnumType.STRING)
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
