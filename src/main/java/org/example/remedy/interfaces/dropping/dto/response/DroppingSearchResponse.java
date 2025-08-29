package org.example.remedy.interfaces.dropping.dto.response;

import org.example.remedy.domain.dropping.Dropping;

public record DroppingSearchResponse(
        String droppingId,
        Long userId,
        String songId,
        Double latitude,
        Double longitude,
        String address
) {
    public static DroppingSearchResponse create(Dropping dropping) {
        return new DroppingSearchResponse(
                dropping.getDroppingId(),
                dropping.getUserId(),
                dropping.getSongId(),
                dropping.getLatitude(),
                dropping.getLongitude(),
                dropping.getAddress()
        );
    }
}
