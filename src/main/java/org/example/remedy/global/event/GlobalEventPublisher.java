package org.example.remedy.global.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.remedy.infrastructure.notification.SseEmitterManager;
import org.springframework.stereotype.Component;

/**
 * SSE 기반 전역 이벤트 발행 관리자
 * 도메인 이벤트를 SSE를 통해 클라이언트에게 실시간으로 전송합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GlobalEventPublisher {

    private final SseEmitterManager sseEmitterManager;

    /**
     * 이벤트를 특정 사용자에게 SSE를 통해 발행합니다.
     *
     * @param userId 수신자 사용자 ID
     * @param eventName 이벤트 이름 (SSE event type)
     * @param eventData 이벤트 데이터 (DTO 객체)
     */
    public void publish(Long userId, String eventName, Object eventData) {
        if (userId == null) {
            log.warn("이벤트 발행 실패: userId가 null입니다. eventName={}", eventName);
            return;
        }

        if (!sseEmitterManager.isConnected(userId)) {
            log.info("사용자가 SSE에 연결되어 있지 않아 이벤트를 발행하지 않습니다 - userId={}, eventName={}", userId, eventName);
            return;
        }

        log.info("SSE 이벤트 발행 - userId={}, eventName={}, data={}", userId, eventName, eventData);
        sseEmitterManager.sendNotification(userId, eventName, eventData);
    }

    /**
     * 여러 사용자에게 동일한 이벤트를 발행합니다.
     *
     * @param userIds 수신자 사용자 ID 목록
     * @param eventName 이벤트 이름
     * @param eventData 이벤트 데이터
     */
    public void publishToMultiple(Iterable<Long> userIds, String eventName, Object eventData) {
        if (userIds == null) {
            log.warn("이벤트 발행 실패: userIds가 null입니다. eventName={}", eventName);
            return;
        }

        userIds.forEach(userId -> publish(userId, eventName, eventData));
    }
}
