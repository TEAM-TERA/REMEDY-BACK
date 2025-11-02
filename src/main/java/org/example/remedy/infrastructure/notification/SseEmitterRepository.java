package org.example.remedy.infrastructure.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Repository
public class SseEmitterRepository {

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter save(Long userId, SseEmitter emitter) {
        emitters.put(userId, emitter);
        log.info("SSE 연결 저장 - userId={}", userId);
        return emitter;
    }

    public void deleteById(Long userId) {
        emitters.remove(userId);
        log.info("SSE 연결 제거 - userId={}", userId);
    }

    public SseEmitter get(Long userId) {
        return emitters.get(userId);
    }

    public boolean exists(Long userId) {
        return emitters.containsKey(userId);
    }
}
