package org.example.remedy.domain.like.application.dto.request;

import jakarta.validation.constraints.NotNull;

public record LikeRequest(
        @NotNull Long droppingId
){}