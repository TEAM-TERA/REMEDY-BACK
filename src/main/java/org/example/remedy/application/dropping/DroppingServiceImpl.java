package org.example.remedy.application.dropping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.remedy.application.dropping.port.in.DroppingService;
import org.example.remedy.application.dropping.port.out.DroppingPersistencePort;
import org.example.remedy.application.dropping.event.DroppingCreatedEvent;
import org.example.remedy.application.song.port.out.SongPersistencePort;
import org.example.remedy.application.user.exception.UserNotFoundException;
import org.example.remedy.application.user.port.out.UserPersistencePort;
import org.example.remedy.domain.dropping.Dropping;
import org.example.remedy.domain.song.Song;
import org.example.remedy.domain.user.User;
import org.example.remedy.global.event.GlobalEventPublisher;
import org.example.remedy.presentation.dropping.dto.request.DroppingCreateRequest;
import org.example.remedy.application.dropping.dto.response.DroppingFindResponse;
import org.example.remedy.application.dropping.dto.response.DroppingSearchListResponse;
import org.example.remedy.application.dropping.dto.response.DroppingSearchResponse;
import org.example.remedy.application.dropping.exception.DroppingNotFoundException;
import org.example.remedy.global.security.auth.AuthDetails;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DroppingServiceImpl implements DroppingService {

    private final DroppingPersistencePort droppingPersistencePort;
    private final SongPersistencePort songPersistencePort;
    private final UserPersistencePort userPersistencePort;
    private final GlobalEventPublisher eventPublisher;

    @Override
    @Transactional
    public void createDropping(AuthDetails authDetails, DroppingCreateRequest request) {
        Dropping dropping = Dropping.getInstance(authDetails.getUserId(), request);
        System.out.println(authDetails.getUserId());
        droppingPersistencePort.createDropping(dropping);

        publishDroppingCreatedEvent(authDetails.getUserId(), dropping.getSongId());
    }

    private void publishDroppingCreatedEvent(Long userId, String songId) {
        DroppingCreatedEvent event = DroppingCreatedEvent.builder()
                .userId(userId)
                .songId(songId)
                .build();

        // SSE를 통해 해당 사용자에게 Dropping 생성 이벤트 발송
        eventPublisher.publish(userId, "dropping-created", event);
    }

    @Override
    public DroppingSearchListResponse searchDroppings(double longitude, double latitude) {
        List<Dropping> allDroppings = droppingPersistencePort
                .findActiveDroppingsWithinRadius(longitude, latitude);

        List<DroppingSearchResponse> droppings = allDroppings.stream()
                .map(dropping -> {
                    String albumImageUrl = getAlbumImageUrl(dropping.getSongId());
                    return DroppingSearchResponse.create(dropping, albumImageUrl);
                })
                .toList();

        return DroppingSearchListResponse.newInstance(droppings);
    }

    @Override
    public DroppingFindResponse getDropping(String droppingId) {
        Dropping dropping = droppingPersistencePort.findById(droppingId)
                .orElseThrow(() -> DroppingNotFoundException.EXCEPTION);

        String username = userPersistencePort.findByUserId(dropping.getUserId())
                .map(User::getUsername)
                .orElseThrow(() -> UserNotFoundException.EXCEPTION);

        String albumImageUrl = getAlbumImageUrl(dropping.getSongId());
        return DroppingFindResponse.newInstance(dropping, username, albumImageUrl);
    }

    @Override
    public List<DroppingSearchResponse> getUserDroppings(Long userId) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        List<Dropping> droppings = droppingPersistencePort.findByUserId(userId, sort);
        
        return droppings.stream()
                .map(dropping -> {
                    String albumImageUrl = getAlbumImageUrl(dropping.getSongId());
                    return DroppingSearchResponse.create(dropping, albumImageUrl);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteDropping(String droppingId, Long userId) {
        Dropping dropping = droppingPersistencePort.findById(droppingId)
                .orElseThrow(() -> DroppingNotFoundException.EXCEPTION);

        if (dropping.getUserId().equals(userId)) droppingPersistencePort.deleteById(droppingId);
    }

    @Override
    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void cleanupExpiredDroppings() {
        List<Dropping> expiredDroppings = droppingPersistencePort.findExpiredAndNotDeletedDroppings(LocalDateTime.now());

        if (expiredDroppings.isEmpty()) return;

        expiredDroppings.forEach(Dropping::markAsDeleted);
        droppingPersistencePort.saveAll(expiredDroppings);

        log.info("만료된 Dropping {}개 자동 soft delete 완료", expiredDroppings.size());
    }

    private String getAlbumImageUrl(String songId) {
        return songPersistencePort.findById(songId)
                .map(Song::getAlbumImagePath)
                .orElse(null);
    }
}
