package org.example.remedy.application.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.remedy.application.notification.exception.FcmTokenNotFoundException;
import org.example.remedy.application.notification.port.in.NotificationService;
import org.example.remedy.application.notification.port.out.NotificationPushPort;
import org.example.remedy.application.user.port.in.UserTokenService;
import org.example.remedy.domain.notification.Notification;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationPushPort notificationPushPort;
    private final UserTokenService userTokenService;

    @Override
    public void sendDropNotification(Long userId, String songId) {

        String fcmToken = userTokenService.findTokenByUserId(userId);
        validateFcmToken(fcmToken, userId);

        Notification notification = Notification.ofDrop(userId, songId);

        notificationPushPort.push(notification, fcmToken);
    }

    @Override
    public void sendLikeNotification(Long userId, String likerUsername, String droppingId) {
        
        String fcmToken = userTokenService.findTokenByUserId(userId);
        validateFcmToken(fcmToken, userId);

        Notification notification = Notification.ofLike(userId, likerUsername, droppingId);

        notificationPushPort.push(notification, fcmToken);
    }

    @Override
    public void sendCommentNotification(Long userId, String commenterUsername, String commentContent) {
        
        String fcmToken = userTokenService.findTokenByUserId(userId);
        validateFcmToken(fcmToken, userId);

        Notification notification = Notification.ofComment(userId, commenterUsername, commentContent);

        notificationPushPort.push(notification, fcmToken);
    }

    private void validateFcmToken(String fcmToken, Long userId) {
        if (fcmToken == null || fcmToken.isEmpty()) {
            log.warn("FCM 토큰이 없어 알림을 전송할 수 없습니다 - userId={}", userId);
            throw FcmTokenNotFoundException.EXCEPTION;
        }
    }
}
