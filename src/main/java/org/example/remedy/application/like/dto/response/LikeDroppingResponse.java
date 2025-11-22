package org.example.remedy.application.like.dto.response;

import org.example.remedy.domain.dropping.DroppingType;

public record LikeDroppingResponse(
        String droppingId,
        DroppingType droppingType,
        String title,
        String imageUrl,
        String address
) {
}