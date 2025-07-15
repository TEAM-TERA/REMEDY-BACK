package org.example.remedy.domain.like.dto.response;

import lombok.Getter;
import org.example.remedy.domain.like.domain.Like;
import org.example.remedy.domain.like.domain.TargetType;

@Getter
public class LikeResponse {

    private final String id;
    private final String targetId;
    private final TargetType targetType;

    private LikeResponse(String id, String targetId, TargetType targetType) {
        this.id = id;
        this.targetId = targetId;
        this.targetType = targetType;
    }

    public static LikeResponse of(Like like) {
        if (like == null) {
            throw new IllegalArgumentException("Like cannot be null");
        }
        return new LikeResponse(
                like.getId(),
                like.getTargetId(),
                like.getTargetType()
        );
    }
}
