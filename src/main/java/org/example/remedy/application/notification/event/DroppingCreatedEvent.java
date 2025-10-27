package org.example.remedy.application.notification.event;

import lombok.Builder;

@Builder
public record DroppingCreatedEvent(
        Long userId, 
        String songId
) {
    public static DroppingCreatedEvent of(Long userId, String songId) {
        return DroppingCreatedEvent.builder()
                .userId(userId)
                .songId(songId)
                .build();
    }
}
