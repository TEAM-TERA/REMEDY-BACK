package org.example.remedy.application.notification.port.out;

import org.example.remedy.domain.notification.Notification;

public interface NotificationPushPort {
    void push(Notification notification);
}
