package org.example.remedy.domain.dropping.application.dto.response;

import org.example.remedy.domain.dropping.domain.Dropping;
import org.example.remedy.domain.dropping.domain.PlaylistDroppingPayload;
import org.example.remedy.domain.song.domain.Song;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

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
