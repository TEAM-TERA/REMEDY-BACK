package org.example.remedy.domain.dropping.application.dto.response;

import java.time.LocalDateTime;

public record MusicDroppingResponse(
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
