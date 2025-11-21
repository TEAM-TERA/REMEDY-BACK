package org.example.remedy.application.dropping.dto.response;

import org.example.remedy.domain.dropping.Dropping;
import org.example.remedy.domain.dropping.DroppingType;
import org.example.remedy.domain.dropping.MusicDroppingPayload;

public record MusicDroppingSearchResponse(
        DroppingType type,
        String droppingId,
        Long userId,
        String songId,
        Double latitude,
        Double longitude,
        String address,
        String albumImageUrl
) implements DroppingResponse {

    public static MusicDroppingSearchResponse from(Dropping dropping, String albumImageUrl) {
        MusicDroppingPayload payload = (MusicDroppingPayload) dropping.getPayload();

        return new MusicDroppingSearchResponse(
                DroppingType.MUSIC,
                dropping.getDroppingId(),
                dropping.getUserId(),
                payload.getSongId(),
                dropping.getLatitude(),
                dropping.getLongitude(),
                dropping.getAddress(),
                albumImageUrl
        );
    }
}