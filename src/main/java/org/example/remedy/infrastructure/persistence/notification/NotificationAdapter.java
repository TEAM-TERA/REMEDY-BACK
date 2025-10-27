package org.example.remedy.infrastructure.persistence.notification;

import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.remedy.application.notification.exception.NotificationSendFailedException;
import org.example.remedy.application.notification.port.out.NotificationPushPort;
import org.example.remedy.domain.notification.Notification;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationAdapter implements NotificationPushPort {

    @Override
    public void push(Notification notification, String fcmToken) {
        Message firebaseMessage = buildFirebaseMessage(notification, fcmToken);
        sendMessage(firebaseMessage, notification);
    }

    private Message buildFirebaseMessage(Notification notification, String fcmToken) {
        return Message.builder()
                .setToken(fcmToken)
                .setNotification(com.google.firebase.messaging.Notification.builder()
                        .setTitle(notification.getTitle())
                        .setBody(notification.getBody())
                        .build())
                .build();
    }

    private void sendMessage(Message firebaseMessage, Notification notification) {
        try {
            String messageId = FirebaseMessaging.getInstance().send(firebaseMessage);
            log.info("알림 전송 완료 - userId={}, title={}, messageId={}", 
                    notification.getUserId(), notification.getTitle(), messageId);
        } catch (FirebaseMessagingException e) {
            log.error("알림 전송 실패 - userId={}, errorCode={}, error={}", 
                    notification.getUserId(), e.getErrorCode(), e.getMessage(), e);
            throw NotificationSendFailedException.EXCEPTION;
        }
    }
}
