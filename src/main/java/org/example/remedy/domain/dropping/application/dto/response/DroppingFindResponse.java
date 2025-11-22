package org.example.remedy.domain.dropping.application.dto.response;

import org.example.remedy.domain.dropping.domain.Dropping;
import java.time.LocalDateTime;

public record DroppingFindResponse(
        String droppingId,
        String songId,
        Long userId,
        String username,
        String content,
        LocalDateTime expiryDate,
        LocalDateTime createdAt,
        String albumImageUrl
) {
    public static DroppingFindResponse newInstance(Dropping dropping, String songId, String username, String albumImageUrl) {
        return new DroppingFindResponse(
                dropping.getDroppingId(),
                songId,
                dropping.getUserId(),
                username,
                dropping.getContent(),
                dropping.getExpiryDate(),
                dropping.getCreatedAt(),
                albumImageUrl
        );
    }
}
