package org.example.remedy.domain.like.application.dto.response;

import org.example.remedy.domain.dropping.domain.DroppingType;

public record VoteLikeDroppingResponse(
        Long droppingId,
        DroppingType droppingType,
        String topic,
        String imageUrl,
        String address
) {
}