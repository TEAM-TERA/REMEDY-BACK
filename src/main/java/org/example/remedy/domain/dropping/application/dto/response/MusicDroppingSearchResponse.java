package org.example.remedy.domain.dropping.application.dto.response;

import org.example.remedy.domain.dropping.domain.DroppingType;

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

}
