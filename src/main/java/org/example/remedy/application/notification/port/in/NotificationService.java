package org.example.remedy.application.notification.port.in;

public interface NotificationService {
    void sendDropNotification(Long userId, String songId);
    void sendLikeNotification(Long userId, String likerUsername, String droppingId);
    void sendCommentNotification(Long userId, String commenterUsername, String commentContent);
}
