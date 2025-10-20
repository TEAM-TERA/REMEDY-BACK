package org.example.remedy.application.notification.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.remedy.application.notification.event.DroppingCreatedEvent;
import org.example.remedy.application.notification.exception.FcmTokenNotFoundException;
import org.example.remedy.application.notification.exception.NotificationSendFailedException;
import org.example.remedy.application.notification.port.in.NotificationService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DroppingEventListener {

    private final NotificationService notificationService;

    @Async
    @EventListener
    public void onDroppingCreated(DroppingCreatedEvent event) {
        try {
            log.info("드랍 생성 알림 전송 시작 - userId={}, songId={}", event.userId(), event.songId());
            notificationService.sendDropNotification(event.userId(), event.songId());
            log.info("드랍 생성 알림 전송 완료 - userId={}", event.userId());
        } catch (FcmTokenNotFoundException e) {
            log.warn("FCM 토큰 없음으로 알림 전송 건너뜀 - userId={}, songId={}", 
                    event.userId(), event.songId());
        } catch (NotificationSendFailedException e) {
            log.error("알림 전송 실패 - userId={}, songId={}, error={}", 
                    event.userId(), event.songId(), e.getMessage());
        } catch (Exception e) {
            log.error("드랍 생성 알림 전송 중 예상치 못한 에러 발생 - userId={}, songId={}", 
                    event.userId(), event.songId(), e);
        }
    }
}
