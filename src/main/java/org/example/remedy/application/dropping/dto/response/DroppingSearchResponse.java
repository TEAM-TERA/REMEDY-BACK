package org.example.remedy.application.dropping.dto.response;

import org.example.remedy.domain.dropping.Dropping;

public record DroppingSearchResponse(
        String droppingId,
        Long userId,
        String songId,
        Double latitude,
        Double longitude,
        String address,
        String albumImageUrl
) {
    public static DroppingSearchResponse create(Dropping dropping, String albumImageUrl) {
        return new DroppingSearchResponse(
                dropping.getDroppingId(),
                dropping.getUserId(),
                dropping.getSongId(),
                dropping.getLatitude(),
                dropping.getLongitude(),
                dropping.getAddress(),
                albumImageUrl
        );
    }
}
