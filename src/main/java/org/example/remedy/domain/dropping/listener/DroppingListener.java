package org.example.remedy.domain.dropping.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.remedy.domain.dropping.domain.Dropping;
import org.example.remedy.domain.dropping.service.DroppingService;
import org.example.remedy.domain.user.dto.response.MyDroppingResponse;
import org.example.remedy.domain.user.event.DroppingEvent;
import org.example.remedy.global.event.DroppingResponseCache;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DroppingListener {

    private final DroppingService droppingService;

    @EventListener
    public void onDroppingEvent(DroppingEvent droppingEvent) {
        try {
            List<Dropping> droppings = droppingService.findDroppingsByUserId(droppingEvent.getUserId());

            List<MyDroppingResponse> converted = droppings.stream()
                    .map(MyDroppingResponse::from)
                    .toList();

            DroppingResponseCache.complete(droppingEvent.getRequestId(), converted);
        } catch (Exception e) {

            DroppingResponseCache.complete(droppingEvent.getRequestId(), List.of());
            log.error("사용자 드롭핑 조회 중 오류 발생: userId={}", droppingEvent.getUserId(), e);
        }

    }
}
