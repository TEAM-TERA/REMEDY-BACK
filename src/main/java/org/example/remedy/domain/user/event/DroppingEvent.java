package org.example.remedy.domain.user.event;

import lombok.Getter;

import java.util.UUID;

@Getter
public class DroppingEvent {
    private final Long userId;
    private final UUID requestId;

    public DroppingEvent(Long userId, UUID requestId) {
        this.userId = userId;
        this.requestId = requestId;
    }
}