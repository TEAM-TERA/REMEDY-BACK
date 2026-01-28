package org.example.remedy.domain.dropping.application.dto.response;

import java.time.LocalDateTime;

public record MusicDroppingResponse(
        Long droppingId,
        String songId,
        Long userId,
        String username,
        String content,
        LocalDateTime expiryDate,
        LocalDateTime createdAt,
        String albumImageUrl
) {
}
