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

    public static Notification ofLike(Long userId, String likerUsername, String droppingId) {
        return Notification.builder()
                .userId(userId)
                .title("Remedy ❤️")
                .body(likerUsername + "님이 회원님의 드랍을 좋아합니다!")
                .build();
    }

    public static Notification ofComment(Long userId, String commenterUsername, String commentContent) {
        String truncatedContent = commentContent.length() > 20 
                ? commentContent.substring(0, 20) + "..." 
                : commentContent;
        
        return Notification.builder()
                .userId(userId)
                .title("Remedy 💬")
                .body(commenterUsername + "님: " + truncatedContent)
                .build();
    }
}
