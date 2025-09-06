package org.example.remedy.presentation.like.dto;

public record LikeRequest(
        Long userId,
        String droppingId
){}