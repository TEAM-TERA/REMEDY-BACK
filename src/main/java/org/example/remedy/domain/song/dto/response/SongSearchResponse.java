package org.example.remedy.domain.song.dto.response;

import org.example.remedy.domain.song.domain.Song;

public record SongSearchResponse(
        String id,
        String title,
        String artist
) {
    public static SongSearchResponse newInstance(Song song){
        return new SongSearchResponse(song.getId(), song.getTitle(), song.getArtist());
    }
}
