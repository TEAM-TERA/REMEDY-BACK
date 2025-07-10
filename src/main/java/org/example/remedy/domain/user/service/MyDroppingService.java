package org.example.remedy.domain.user.service;

import lombok.RequiredArgsConstructor;

import org.example.remedy.domain.user.dto.response.MyDroppingResponse;
import org.example.remedy.domain.user.event.DroppingEvent;
import org.example.remedy.global.event.DroppingResponseCache;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MyDroppingService {

    private final ApplicationEventPublisher publisher;

    public List<MyDroppingResponse> getMyDroppings(Long userId) {
        UUID requestId = UUID.randomUUID();
        DroppingEvent event = new DroppingEvent(userId, requestId);

        publisher.publishEvent(event);

        for (int i = 0; i < 10; i++) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            if (DroppingResponseCache.contains(requestId)) {
                return DroppingResponseCache.consume(requestId);
            }
        }

        return List.of();
    }
}
