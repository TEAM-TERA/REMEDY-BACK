package org.example.remedy.global.event;

import org.example.remedy.domain.user.dto.response.MyDroppingResponse;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DroppingResponseCache {
    private static final Map<UUID, List<MyDroppingResponse>> cache = new ConcurrentHashMap<>();

    public static void save(UUID requestId, List<MyDroppingResponse> response) {
        cache.put(requestId, response);
    }

    public static boolean contains(UUID requestId) {
        return cache.containsKey(requestId);
    }

    public static List<MyDroppingResponse> consume(UUID requestId) {
        return cache.remove(requestId);
    }
}
