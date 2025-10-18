package org.example.remedy.application.notification.listener;

import lombok.RequiredArgsConstructor;
import org.example.remedy.application.notification.event.DroppingCreatedEvent;
import org.example.remedy.application.notification.port.in.NotificationService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DroppingEventListener {

    private final NotificationService notificationService;

    @Async
    @EventListener
    public void onDroppingCreated(DroppingCreatedEvent event) {
        notificationService.sendDropNotification(event.userId(), event.songId());
    }
}
