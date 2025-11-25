package org.example.remedy.domain.like.application.dto.response;

import org.example.remedy.domain.dropping.domain.DroppingType;

public record MusicLikeDroppingResponse(
        String droppingId,
        DroppingType droppingType,
        String title,
        String artist,
        String imageUrl,
        String address
) {
}