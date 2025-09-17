package org.example.remedy.presentation.running.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record RunningRequest(

        @Min(value = 0, message = "거리는 0 이상이어야 합니다.")
        double distanceKm,

        @Min(value = 1, message = "시간은 최소 1초 이상이어야 합니다.")
        int durationSec,

        String songId,

        @NotNull(message = "시작 시간은 필수입니다.")
        LocalDateTime startedAt,

        @NotNull(message = "종료 시간은 필수입니다.")
        LocalDateTime endedAt
) {}

