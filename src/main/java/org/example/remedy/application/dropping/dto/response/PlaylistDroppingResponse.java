package org.example.remedy.application.dropping.dto.response;

import org.example.remedy.domain.dropping.Dropping;
import org.example.remedy.domain.dropping.PlaylistDroppingPayload;
import org.example.remedy.domain.song.Song;

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

    public static PlaylistDroppingResponse from(
            Dropping dropping,
            Function<String, Song> songFinder
    ) {
        PlaylistDroppingPayload payload = (PlaylistDroppingPayload) dropping.getPayload();

        List<SongInfo> songs = payload.getSongIds().stream()
                .map(songFinder)
                .map(song -> new SongInfo(
                        song.getId(),
                        song.getTitle(),
                        song.getArtist(),
                        song.getAlbumImagePath()
                ))
                .toList();

        return new PlaylistDroppingResponse(
                dropping.getDroppingId(),
                dropping.getUserId(),
                payload.getPlaylistName(),
                songs,
                dropping.getContent(),
                dropping.getLatitude(),
                dropping.getLongitude(),
                dropping.getAddress(),
                dropping.getExpiryDate(),
                dropping.getCreatedAt()
        );
    }

    public record SongInfo(
            String songId,
            String title,
            String artist,
            String albumImagePath
    ) {}
}
