package org.example.remedy.domain.like.application.dto.response;

import org.example.remedy.domain.dropping.domain.DroppingType;

public record LikeDroppingResponse(
        String droppingId,
        DroppingType droppingType,
        String title,
        String imageUrl,
        String address
) {
}