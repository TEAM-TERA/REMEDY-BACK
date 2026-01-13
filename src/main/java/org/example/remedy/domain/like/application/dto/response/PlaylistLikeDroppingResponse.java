package org.example.remedy.domain.like.application.dto.response;

import org.example.remedy.domain.dropping.domain.DroppingType;

public record PlaylistLikeDroppingResponse(
        Long droppingId,
        DroppingType droppingType,
        String playlistName,
        String imageUrl,
        String address
) {
}