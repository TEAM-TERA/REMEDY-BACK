package org.example.remedy.domain.like.dto.request;

public record LikeRequest(
        Long userId,
        String droppingId
){}