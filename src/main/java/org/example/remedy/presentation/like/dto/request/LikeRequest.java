package org.example.remedy.presentation.like.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LikeRequest(
        @NotBlank String droppingId
){}