package org.example.remedy.domain.dropping.application.dto.response;

import org.example.remedy.domain.dropping.domain.Dropping;
import org.example.remedy.domain.dropping.domain.DroppingType;
import org.example.remedy.domain.dropping.domain.MusicDroppingPayload;

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