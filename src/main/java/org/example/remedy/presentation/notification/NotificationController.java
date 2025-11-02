package org.example.remedy.presentation.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.remedy.global.security.auth.AuthDetails;
import org.example.remedy.infrastructure.notification.SseEmitterManager;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final SseEmitterManager sseEmitterManager;

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@AuthenticationPrincipal AuthDetails authDetails) {
        Long userId = authDetails.getUser().getUserId();
        log.info("SSE 구독 요청 - userId={}", userId);

        return sseEmitterManager.createEmitter(userId);
    }
}
