package org.example.remedy.domain.dropping.application.dto.response;

import org.example.remedy.domain.dropping.domain.Dropping;
import org.example.remedy.domain.dropping.domain.DroppingType;
import org.example.remedy.domain.dropping.domain.PlaylistDroppingPayload;

import java.util.List;

public record PlaylistDroppingSearchResponse(
        DroppingType type,
        String droppingId,
        Long userId,
        String playlistName,
        List<String> songIds,
        Double latitude,
        Double longitude,
        String address
) implements DroppingResponse {

    public static PlaylistDroppingSearchResponse from(Dropping dropping) {
        PlaylistDroppingPayload payload = (PlaylistDroppingPayload) dropping.getPayload();

        return new PlaylistDroppingSearchResponse(
                DroppingType.PLAYLIST,
                dropping.getDroppingId(),
                dropping.getUserId(),
                payload.getPlaylistName(),
                payload.getSongIds(),
                dropping.getLatitude(),
                dropping.getLongitude(),
                dropping.getAddress()
        );
    }
}