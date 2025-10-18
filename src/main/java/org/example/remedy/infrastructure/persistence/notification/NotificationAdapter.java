package org.example.remedy.infrastructure.persistence.notification;

import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.remedy.application.notification.port.out.NotificationPushPort;
import org.example.remedy.application.user.port.in.UserTokenService;
import org.example.remedy.domain.notification.Notification;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationAdapter implements NotificationPushPort {

    private final UserTokenService userTokenService;

    @Override
    public void push(Notification notification) {
        String token = userTokenService.findTokenByUserId(notification.getUserId());
        if (token == null) {
            log.warn("FCM 토큰 없음 - userId={}", notification.getUserId());
            return;
        }

        Message firebaseMessage = Message.builder()
                .setToken(token)
                .setNotification(com.google.firebase.messaging.Notification.builder()
                        .setTitle(notification.getTitle())
                        .setBody(notification.getBody())
                        .build())
                .build();

        try {
            FirebaseMessaging.getInstance().send(firebaseMessage);
            log.info("알림 전송 완료: userId={}, title={}", notification.getUserId(), notification.getTitle());
        } catch (FirebaseMessagingException e) {
            log.error("알림 전송 실패", e);
        }
    }
}