package org.example.remedy.domain.like.dto.response;

import lombok.Getter;
import org.example.remedy.domain.like.domain.Like;
import org.example.remedy.domain.like.domain.TargetType;

import java.time.LocalDateTime;

@Getter
public class LikeResponse {

    private final Long id;
    private final String targetId;
    private final TargetType targetType;

    private LikeResponse(Long id, String targetId, TargetType targetType) {
        this.id = id;
        this.targetId = targetId;
        this.targetType = targetType;
    }

    public static LikeResponse of(Like like) {
        return new LikeResponse(
                like.getId(),
                like.getTargetId(),
                like.getTargetType()
        );
    }
}
