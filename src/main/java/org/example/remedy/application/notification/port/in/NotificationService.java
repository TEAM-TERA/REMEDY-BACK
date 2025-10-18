package org.example.remedy.application.notification.port.in;

public interface NotificationService {
    void sendDropNotification(Long userId, String songId);
}
