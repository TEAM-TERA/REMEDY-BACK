package org.example.remedy.domain.song.application.dto.response;

import org.example.remedy.domain.song.domain.Song;

public record SongResponse(
        String id,
        String title,
        String artist,
        int duration,
        String albumImagePath
) {
}
