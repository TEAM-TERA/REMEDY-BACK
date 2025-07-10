package org.example.remedy.domain.dropping.listener;

import lombok.RequiredArgsConstructor;
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
public class DroppingListener {

    private final DroppingService droppingService;

    @EventListener
    public void onDroppingEvent(DroppingEvent droppingEvent) {
        List<Dropping> droppings = droppingService.findDroppingsByUserId(droppingEvent.getUserId());

        List<MyDroppingResponse> converted = droppings.stream()
                .map(MyDroppingResponse::from)
                .toList();

        DroppingResponseCache.save(droppingEvent.getRequestId(), converted);
    }
}
