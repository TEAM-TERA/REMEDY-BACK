package org.example.remedy.domain.song.application.dto.response;

import org.example.remedy.domain.song.domain.Song;

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
