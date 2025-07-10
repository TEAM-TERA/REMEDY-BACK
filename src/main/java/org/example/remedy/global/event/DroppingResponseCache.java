package org.example.remedy.global.event;

import org.example.remedy.domain.user.dto.response.MyDroppingResponse;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class DroppingResponseCache {
    private static final Map<UUID, CompletableFuture<List<MyDroppingResponse>>> cache = new ConcurrentHashMap<>();

    public static void register(UUID requestId, CompletableFuture<List<MyDroppingResponse>> future) {
        cache.put(requestId, future);
    }

    public static void complete(UUID requestId, List<MyDroppingResponse> response) {
        CompletableFuture<List<MyDroppingResponse>> future = cache.remove(requestId);
        if (future != null) {
            future.complete(response);
        }
    }
}
