package org.example.remedy.domain.song.application.dto.response;

import org.example.remedy.domain.song.domain.Song;

public record SongResponse(
        String id,
        String title,
        String artist,
        int duration,
        String albumImagePath
) {
    public static SongResponse newInstance(Song song) {
        return new SongResponse(song.getId(), song.getTitle(), song.getArtist(), song.getDuration(), song.getAlbumImagePath());
    }
}
