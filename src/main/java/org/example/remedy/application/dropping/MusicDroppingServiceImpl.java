package org.example.remedy.application.dropping;

import lombok.RequiredArgsConstructor;
import org.example.remedy.application.dropping.event.DroppingCreatedEvent;
import org.example.remedy.application.dropping.port.in.MusicDroppingService;
import org.example.remedy.application.dropping.port.out.DroppingPersistencePort;
import org.example.remedy.domain.dropping.Dropping;
import org.example.remedy.domain.dropping.DroppingType;
import org.example.remedy.domain.dropping.MusicDroppingPayload;
import org.example.remedy.global.event.GlobalEventPublisher;
import org.example.remedy.global.security.auth.AuthDetails;
import org.example.remedy.presentation.dropping.dto.request.DroppingCreateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MusicDroppingServiceImpl implements MusicDroppingService {

    private final DroppingPersistencePort droppingPersistencePort;
    private final GlobalEventPublisher eventPublisher;

    @Override
    @Transactional
    public void createDropping(AuthDetails authDetails, DroppingCreateRequest request) {
        LocalDateTime now = LocalDateTime.now();

        MusicDroppingPayload payload = MusicDroppingPayload.builder()
                .songId(request.songId())
                .build();

        Dropping dropping = new Dropping(
                DroppingType.MUSIC,
                payload,
                authDetails.getUserId(),
                request.content(),
                request.latitude(),
                request.longitude(),
                request.address(),
                now.plusDays(3),
                now,
                false
        );

        droppingPersistencePort.createDropping(dropping);

        publishDroppingCreatedEvent(authDetails.getUserId(), payload.getSongId());
    }

    private void publishDroppingCreatedEvent(Long userId, String songId) {
        DroppingCreatedEvent event = DroppingCreatedEvent.builder()
                .userId(userId)
                .songId(songId)
                .build();
        eventPublisher.publish(userId, "dropping-created", event);
    }
}