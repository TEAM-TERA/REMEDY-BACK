package org.example.remedy.domain.dropping.application.service;

import lombok.RequiredArgsConstructor;
import org.example.remedy.domain.dropping.application.event.DroppingCreatedEvent;
import org.example.remedy.domain.dropping.repository.DroppingRepository;
import org.example.remedy.domain.dropping.domain.Dropping;
import org.example.remedy.domain.dropping.domain.DroppingType;
import org.example.remedy.domain.dropping.domain.MusicDroppingPayload;
import org.example.remedy.global.event.GlobalEventPublisher;
import org.example.remedy.global.security.auth.AuthDetails;
import org.example.remedy.domain.dropping.application.dto.request.DroppingCreateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MusicDroppingService {

    private final DroppingRepository droppingRepository;
    private final GlobalEventPublisher eventPublisher;

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

        droppingRepository.createDropping(dropping);

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