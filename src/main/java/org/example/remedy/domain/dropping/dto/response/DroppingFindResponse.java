package org.example.remedy.domain.dropping.dto.response;

import org.example.remedy.domain.dropping.domain.Dropping;
import java.time.LocalDateTime;

public record DroppingFindResponse(
        String droppingId,
        String songId,
        Long userId,
        String content,
        LocalDateTime expiryDate,
        LocalDateTime createdAt
) {
    public static DroppingFindResponse newInstance(Dropping dropping) {
        return new DroppingFindResponse(
                dropping.getDroppingId(),
                dropping.getSongId(),
                dropping.getUserId(),
                dropping.getContent(),
                dropping.getExpiryDate(),
                dropping.getCreatedAt()
        );
    }
}