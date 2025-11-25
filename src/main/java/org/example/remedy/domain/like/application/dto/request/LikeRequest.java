package org.example.remedy.domain.like.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LikeRequest(
        @NotBlank String droppingId
){}