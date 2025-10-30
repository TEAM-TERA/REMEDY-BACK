package org.example.remedy.application.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.remedy.application.notification.port.in.NotificationService;
import org.example.remedy.application.notification.port.out.NotificationPushPort;
import org.example.remedy.domain.notification.Notification;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationPushPort notificationPushPort;

    @Override
    public void sendDropNotification(Long userId, String songId) {
        Notification notification = Notification.ofDrop(userId, songId);
        notificationPushPort.push(notification);
    }

    @Override
    public void sendLikeNotification(Long userId, String likerUsername, String droppingId) {
        Notification notification = Notification.ofLike(userId, likerUsername, droppingId);
        notificationPushPort.push(notification);
    }

    @Override
    public void sendCommentNotification(Long userId, String commenterUsername, String commentContent) {
        Notification notification = Notification.ofComment(userId, commenterUsername, commentContent);
        notificationPushPort.push(notification);
    }
}
