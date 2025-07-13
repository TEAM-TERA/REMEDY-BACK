package org.example.remedy.domain.song.dto.response;

import org.example.remedy.domain.song.domain.Song;

public record SongResponse(
        String id,
        String title,
        String artist,
        int duration
) {
    public static SongResponse newInstance(Song song) {
        return new SongResponse(song.getId(), song.getTitle(), song.getArtist(), song.getDuration());
    }
}
