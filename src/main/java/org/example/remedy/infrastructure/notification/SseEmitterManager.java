package org.example.remedy.infrastructure.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class SseEmitterManager {

    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60; // 60 minutes

    private final SseEmitterRepository emitterRepository;

    public SseEmitter createEmitter(Long userId) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);

        emitter.onCompletion(() -> {
            log.info("SSE 연결 완료 - userId={}", userId);
            emitterRepository.deleteById(userId);
        });

        emitter.onTimeout(() -> {
            log.info("SSE 연결 타임아웃 - userId={}", userId);
            emitter.complete();
            emitterRepository.deleteById(userId);
        });

        emitter.onError((error) -> {
            log.error("SSE 연결 에러 - userId={}, error={}", userId, error.getMessage());
            emitter.completeWithError(error);
            emitterRepository.deleteById(userId);
        });

        emitterRepository.save(userId, emitter);

        // 연결 직후 더미 이벤트 전송 (503 에러 방지)
        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("Connected"));
        } catch (IOException e) {
            log.error("SSE 초기 이벤트 전송 실패 - userId={}", userId, e);
            emitterRepository.deleteById(userId);
            throw new RuntimeException("SSE 연결 초기화 실패", e);
        }

        return emitter;
    }

    public void sendNotification(Long userId, String eventName, Object data) {
        SseEmitter emitter = emitterRepository.get(userId);

        if (emitter == null) {
            log.warn("SSE 연결이 없어 알림을 전송할 수 없습니다 - userId={}", userId);
            return;
        }

        try {
            emitter.send(SseEmitter.event()
                    .name(eventName)
                    .data(data));
            log.info("SSE 알림 전송 완료 - userId={}, event={}", userId, eventName);
        } catch (IOException e) {
            log.error("SSE 알림 전송 실패 - userId={}, event={}", userId, eventName, e);
            emitterRepository.deleteById(userId);
        }
    }

    public boolean isConnected(Long userId) {
        return emitterRepository.exists(userId);
    }
}
