package org.example.remedy.domain.song.application.dto.response;

public record SongResponse(
        String id,
        String title,
        String artist,
        int duration,
        String albumImagePath
) {
}
