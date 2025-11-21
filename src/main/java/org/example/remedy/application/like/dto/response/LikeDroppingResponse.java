package org.example.remedy.application.like.dto.response;

public record LikeDroppingResponse(
        String droppingId,
        String songId,
        String songTitle,
        String albumImageUrl,
        String address
) {
}