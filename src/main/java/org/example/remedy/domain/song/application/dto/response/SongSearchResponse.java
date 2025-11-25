package org.example.remedy.domain.song.application.dto.response;

public record SongSearchResponse(
        String id,
        String title,
        String artist,
        String albumImagePath
) {
}
