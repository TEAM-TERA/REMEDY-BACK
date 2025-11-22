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
}
