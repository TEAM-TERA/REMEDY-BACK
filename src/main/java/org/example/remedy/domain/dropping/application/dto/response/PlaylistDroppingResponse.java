package org.example.remedy.domain.dropping.application.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record PlaylistDroppingResponse(
        String droppingId,
        Long userId,
        String playlistName,
        List<SongInfo> songs,
        String content,
        Double latitude,
        Double longitude,
        String address,
        LocalDateTime expiryDate,
        LocalDateTime createdAt
) {

    public record SongInfo(
            String songId,
            String title,
            String artist,
            String albumImagePath
    ) {}
}
