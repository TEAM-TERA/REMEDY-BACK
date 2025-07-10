package org.example.remedy.global.event;

import org.example.remedy.domain.user.dto.response.MyDroppingResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

@Component
public class DroppingResponseCache {
    private final Map<UUID, CompletableFuture<List<MyDroppingResponse>>> cache = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public DroppingResponseCache() {
        scheduler.scheduleAtFixedRate(this::cleanupExpired, 5, 5, TimeUnit.MINUTES);
    }

    public void register(UUID requestId, CompletableFuture<List<MyDroppingResponse>> future) {
        if (cache.size() > 1000) {
            throw new IllegalStateException("캐시가 가득 찼습니다");
        }

        cache.put(requestId, future);

        scheduler.schedule(() -> {
            CompletableFuture<List<MyDroppingResponse>> expired = cache.remove(requestId);
            if (expired != null && !expired.isDone()) {
                expired.completeExceptionally(new TimeoutException("요청 타임아웃"));
            }
        }, 2, TimeUnit.MINUTES);
    }

    public void complete(UUID requestId, List<MyDroppingResponse> response) {
        CompletableFuture<List<MyDroppingResponse>> future = cache.remove(requestId);
        if (future != null) {
            future.complete(response);
        }
    }

    private void cleanupExpired() {
        cache.entrySet().removeIf(entry ->
                entry.getValue().isDone() || entry.getValue().isCancelled());
    }
}
