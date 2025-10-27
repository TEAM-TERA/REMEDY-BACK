package org.example.remedy.presentation.running.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record RunningRequest(

        @DecimalMin(value = "0.0", inclusive = true, message = "거리는 0 이상이어야 합니다.")
        double distanceKm,

        @Positive(message = "시간은 최소 1초 이상이어야 합니다.")
        int durationSec,

        String songId,

        @NotNull(message = "시작 시간은 필수입니다.")
        LocalDateTime startedAt,

        @NotNull(message = "종료 시간은 필수입니다.")
        LocalDateTime endedAt
) {}

