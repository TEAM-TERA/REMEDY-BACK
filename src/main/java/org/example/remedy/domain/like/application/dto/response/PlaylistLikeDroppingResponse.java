package org.example.remedy.domain.like.application.dto.response;

import org.example.remedy.domain.dropping.domain.DroppingType;

public record PlaylistLikeDroppingResponse(
        String droppingId,
        DroppingType droppingType,
        String playlistName,
        String imageUrl,
        String address
) {
}