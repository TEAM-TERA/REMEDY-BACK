package org.example.remedy.infrastructure.persistence.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.remedy.application.notification.exception.NotificationSendFailedException;
import org.example.remedy.application.notification.port.out.NotificationPushPort;
import org.example.remedy.domain.notification.Notification;
import org.example.remedy.infrastructure.notification.SseEmitterManager;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationAdapter implements NotificationPushPort {

    private final SseEmitterManager sseEmitterManager;

    @Override
    public void push(Notification notification) {
        try {
            if (!sseEmitterManager.isConnected(notification.getUserId())) {
                log.warn("SSE 연결이 없어 알림을 전송할 수 없습니다 - userId={}", notification.getUserId());
                return;
            }

            sseEmitterManager.sendNotification(
                    notification.getUserId(),
                    "notification",
                    notification
            );

            log.info("알림 전송 완료 - userId={}, title={}",
                    notification.getUserId(), notification.getTitle());
        } catch (Exception e) {
            log.error("알림 전송 실패 - userId={}, error={}",
                    notification.getUserId(), e.getMessage(), e);
            throw NotificationSendFailedException.EXCEPTION;
        }
    }
}
