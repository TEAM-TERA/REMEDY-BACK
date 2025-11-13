package org.example.remedy.application.song.dto.response;

import org.example.remedy.domain.song.Song;

public record SongResponse(
        String id,
        String title,
        String artist,
        int duration,
        String hlsPath,
        String albumImagePath
) {
    public static SongResponse newInstance(Song song) {
        return new SongResponse(song.getId(), song.getTitle(), song.getArtist(), song.getDuration(), song.getHlsPath(), song.getAlbumImagePath());
    }
}
