package org.example.remedy.application.song.dto.response;

import org.example.remedy.domain.song.Song;

public record SongSearchResponse(
        String id,
        String title,
        String artist,
        String albumImagePath
) {
    public static SongSearchResponse newInstance(Song song){
        return new SongSearchResponse(song.getId(), song.getTitle(), song.getArtist(), song.getAlbumImagePath());
    }
}
