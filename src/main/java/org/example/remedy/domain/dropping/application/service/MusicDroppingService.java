package org.example.remedy.domain.dropping.application.service;

import lombok.RequiredArgsConstructor;
import org.example.remedy.domain.dropping.application.dto.request.DroppingCreateRequest;
import org.example.remedy.domain.dropping.application.dto.response.DroppingFindResponse;
import org.example.remedy.domain.dropping.application.event.DroppingCreatedEvent;
import org.example.remedy.domain.dropping.application.exception.DroppingNotFoundException;
import org.example.remedy.domain.dropping.application.mapper.DroppingMapper;
import org.example.remedy.domain.dropping.domain.Dropping;
import org.example.remedy.domain.dropping.domain.MusicDroppingPayload;
import org.example.remedy.domain.dropping.repository.DroppingRepository;
import org.example.remedy.domain.song.application.exception.SongNotFoundException;
import org.example.remedy.domain.song.repository.SongRepository;
import org.example.remedy.domain.user.application.exception.UserNotFoundException;
import org.example.remedy.domain.user.domain.User;
import org.example.remedy.domain.user.repository.UserRepository;
import org.example.remedy.global.event.GlobalEventPublisher;
import org.example.remedy.global.security.auth.AuthDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MusicDroppingService {

    private final DroppingRepository droppingRepository;
    private final SongRepository songRepository;
    private final UserRepository userRepository;
    private final GlobalEventPublisher eventPublisher;

    @Transactional
    public void createDropping(AuthDetails authDetails, DroppingCreateRequest request) {
        MusicDroppingPayload payload = DroppingMapper.toPayload(request);
        Dropping dropping = DroppingMapper.toEntity(authDetails, request, payload);

        droppingRepository.createDropping(dropping);

        publishDroppingCreatedEvent(authDetails.getUserId(), payload.getSongId());
    }

    public DroppingFindResponse getMusicDropping(String droppingId) {
        Dropping dropping = droppingRepository.findById(droppingId)
                .orElseThrow(() -> DroppingNotFoundException.EXCEPTION);

        String username = userRepository.findByUserId(dropping.getUserId())
                .map(User::getUsername)
                .orElseThrow(() -> UserNotFoundException.EXCEPTION);

        MusicDroppingPayload payload = (MusicDroppingPayload) dropping.getPayload();
        String albumImageUrl = songRepository.findById(payload.getSongId())
                .orElseThrow(() -> SongNotFoundException.EXCEPTION)
                .getAlbumImagePath();

        return DroppingMapper.toDroppingFindResponse(dropping, payload.getSongId(), username, albumImageUrl);
    }

    private void publishDroppingCreatedEvent(Long userId, String songId) {
        DroppingCreatedEvent event = DroppingCreatedEvent.builder()
                .userId(userId)
                .songId(songId)
                .build();
        eventPublisher.publish(userId, "dropping-created", event);
    }
}
