package org.example.remedy.domain.dropping.application.dto.response;

import org.example.remedy.domain.dropping.domain.DroppingType;

import java.util.List;

public record VoteDroppingSearchResponse(
        DroppingType type,
        String droppingId,
        Long userId,
        String topic,
        List<String> options,
        String content,
        Double latitude,
        Double longitude,
        String address,
        String firstAlbumImageUrl,
		boolean isMyDropping
) implements DroppingResponse {

}
