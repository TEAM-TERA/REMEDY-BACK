package org.example.remedy.application.notification;

import lombok.RequiredArgsConstructor;
import org.example.remedy.application.notification.port.in.NotificationService;
import org.example.remedy.application.notification.port.out.NotificationPushPort;
import org.example.remedy.domain.notification.Notification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationPushPort notificationPushPort;


    @Override
    public void sendDropNotification(Long userId, String songId) {
        Notification notification = Notification.ofDrop(userId, songId);
        notificationPushPort.push(notification);
    }
}
