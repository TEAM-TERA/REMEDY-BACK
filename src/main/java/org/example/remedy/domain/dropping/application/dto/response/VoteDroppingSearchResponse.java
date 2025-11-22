package org.example.remedy.domain.dropping.application.dto.response;

import org.example.remedy.domain.dropping.domain.DroppingType;

import java.util.List;

public record VoteDroppingSearchResponse(
        DroppingType type,
        String droppingId,
        Long userId,
        String topic,
        List<String> options,
        Double latitude,
        Double longitude,
        String address
) implements DroppingResponse {

}
