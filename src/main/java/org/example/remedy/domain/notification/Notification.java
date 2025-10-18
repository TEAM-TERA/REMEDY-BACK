package org.example.remedy.domain.notification;

import lombok.*;


@Getter
@Builder
public class Notification {

    private final Long userId;
    private final String title;
    private final String body;

    public static Notification ofDrop(Long userId, String songId) {
        return Notification.builder()
                .userId(userId)
                .title("Remedy 🎵")
                .body("드랍이 완료되었습니다! (" + songId + ")")
                .build();
    }
}
