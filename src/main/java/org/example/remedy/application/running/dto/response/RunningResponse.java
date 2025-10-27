package org.example.remedy.application.running.dto.response;

import org.example.remedy.domain.running.Running;

import java.time.LocalDateTime;

public record RunningResponse(
        Long id,
        Long userId,
        double distanceKm,
        int durationSec,
        String songId,
        LocalDateTime startedAt,
        LocalDateTime endedAt
) {
    public static RunningResponse from(Running running) {
        return new RunningResponse(
                running.getId(),
                running.getUser().getUserId(),
                running.getDistanceKm(),
                running.getDurationSec(),
                running.getSongId(),
                running.getStartedAt(),
                running.getEndedAt()
        );
    }
}