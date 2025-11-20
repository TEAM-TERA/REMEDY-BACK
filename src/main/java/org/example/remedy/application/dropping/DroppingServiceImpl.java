package org.example.remedy.application.dropping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.remedy.application.dropping.exception.InvalidDroppingDeleteRequestException;
import org.example.remedy.application.dropping.port.in.DroppingService;
import org.example.remedy.application.dropping.port.out.DroppingPersistencePort;
import org.example.remedy.application.dropping.event.DroppingCreatedEvent;
import org.example.remedy.application.song.port.out.SongPersistencePort;
import org.example.remedy.application.user.exception.UserNotFoundException;
import org.example.remedy.application.user.port.out.UserPersistencePort;
import org.example.remedy.domain.dropping.Dropping;
import org.example.remedy.domain.dropping.DroppingType;
import org.example.remedy.domain.dropping.MusicDroppingPayload;
import org.example.remedy.domain.dropping.VoteDroppingPayload;
import org.example.remedy.domain.song.Song;
import org.example.remedy.domain.user.User;
import org.example.remedy.global.event.GlobalEventPublisher;
import org.example.remedy.presentation.dropping.dto.request.DroppingCreateRequest;
import org.example.remedy.presentation.dropping.dto.request.VoteDroppingCreateRequest;
import org.example.remedy.application.dropping.dto.response.DroppingFindResponse;
import org.example.remedy.application.dropping.dto.response.DroppingResponse;
import org.example.remedy.application.dropping.dto.response.DroppingSearchListResponse;
import org.example.remedy.application.dropping.dto.response.MusicDroppingSearchResponse;
import org.example.remedy.application.dropping.dto.response.VoteDroppingSearchResponse;
import org.example.remedy.application.dropping.dto.response.VoteDroppingResponse;
import org.example.remedy.application.dropping.exception.DroppingNotFoundException;
import org.example.remedy.global.security.auth.AuthDetails;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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

        List<DroppingResponse> droppings = convertToResponseList(allDroppings);
        return DroppingSearchListResponse.of(droppings);
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
    public DroppingSearchListResponse getUserDroppings(Long userId) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        List<Dropping> allDroppings = droppingPersistencePort.findByUserId(userId, sort);

        List<DroppingResponse> droppings = convertToResponseList(allDroppings);
        return DroppingSearchListResponse.of(droppings);
    }

    @Override
    @Transactional
    public void deleteDropping(String droppingId, Long userId) {
        Dropping dropping = droppingPersistencePort.findById(droppingId)
                .orElseThrow(() -> DroppingNotFoundException.EXCEPTION);

        if (!dropping.getUserId().equals(userId)) {
            throw new InvalidDroppingDeleteRequestException();
        }

        droppingPersistencePort.softDelete(dropping);
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

    private List<DroppingResponse> convertToResponseList(List<Dropping> droppings) {
        return droppings.stream()
                .map(dropping -> {
                    String albumImageUrl = getAlbumImageUrl(dropping.getSongId());
                    return switch (dropping.getDroppingType()) {
                        case MUSIC -> MusicDroppingSearchResponse.from(dropping, albumImageUrl);
                        case VOTE -> VoteDroppingSearchResponse.from(dropping, albumImageUrl);
                    };
                })
                .collect(Collectors.toList());
    }

    private String getAlbumImageUrl(String songId) {
        return songPersistencePort.findById(songId)
                .map(Song::getAlbumImagePath)
                .orElse(null);
    }

    @Override
    @Transactional
    public void createVoteDropping(AuthDetails authDetails, VoteDroppingCreateRequest request) {
        LocalDateTime now = LocalDateTime.now();

        LinkedHashMap<String, List<Long>> optionVotes = new LinkedHashMap<>();
        for (String songId : request.options()) {
            optionVotes.put(songId, new ArrayList<>());
        }

        VoteDroppingPayload payload = VoteDroppingPayload.builder()
                .topic(request.topic())
                .songId(request.songId())
                .optionVotes(optionVotes)
                .build();

        Dropping dropping = new Dropping(
                DroppingType.VOTE,
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

        publishDroppingCreatedEvent(authDetails.getUserId(), dropping.getSongId());
    }

    @Override
    @Transactional
    public void vote(String droppingId, Long userId, String songId) {
        Dropping dropping = droppingPersistencePort.findById(droppingId)
                .orElseThrow(() -> DroppingNotFoundException.EXCEPTION);

        dropping.vote(userId, songId);
        droppingPersistencePort.save(dropping);
    }

    @Override
    @Transactional
    public void cancelVote(String droppingId, Long userId) {
        Dropping dropping = droppingPersistencePort.findById(droppingId)
                .orElseThrow(() -> DroppingNotFoundException.EXCEPTION);

        dropping.cancelVote(userId);
        droppingPersistencePort.save(dropping);
    }

    @Override
    public VoteDroppingResponse getVoteDropping(String droppingId, Long userId) {
        Dropping dropping = droppingPersistencePort.findById(droppingId)
                .orElseThrow(() -> DroppingNotFoundException.EXCEPTION);

        return VoteDroppingResponse.from(
                dropping,
                userId,
                songId -> songPersistencePort.findById(songId).orElse(null)
        );
    }
}
