package org.example.remedy.domain.user.service;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.example.remedy.domain.user.dto.response.MyDroppingResponse;
import org.example.remedy.domain.user.event.DroppingEvent;
import org.example.remedy.global.event.DroppingResponseCache;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class MyDroppingService {

    private final ApplicationEventPublisher publisher;

    public CompletableFuture<List<MyDroppingResponse>> getMyDroppings(Long userId) {
        UUID requestId = UUID.randomUUID();
        DroppingEvent event = new DroppingEvent(userId, requestId);

        CompletableFuture<List<MyDroppingResponse>> future = new CompletableFuture<>();
        DroppingResponseCache.register(requestId, future);

        publisher.publishEvent(event);

        CompletableFuture.delayedExecutor(1, TimeUnit.SECONDS).execute(() -> {
            if (!future.isDone()) {
                future.complete(List.of());
                log.warn("드롭핑 조회 타임아웃 발생: userId={}", userId);
            }
        });

        return future;
    }
}
