package org.example.remedy.application.dropping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.remedy.application.dropping.dto.response.DroppingFindResponse;
import org.example.remedy.application.dropping.dto.response.DroppingResponse;
import org.example.remedy.application.dropping.dto.response.DroppingSearchListResponse;
import org.example.remedy.application.dropping.dto.response.MusicDroppingSearchResponse;
import org.example.remedy.application.dropping.dto.response.PlaylistDroppingSearchResponse;
import org.example.remedy.application.dropping.dto.response.VoteDroppingSearchResponse;
import org.example.remedy.application.dropping.exception.DroppingNotFoundException;
import org.example.remedy.application.dropping.exception.InvalidDroppingDeleteRequestException;
import org.example.remedy.application.dropping.port.in.DroppingService;
import org.example.remedy.application.dropping.port.out.DroppingPersistencePort;
import org.example.remedy.application.song.exception.SongNotFoundException;
import org.example.remedy.application.song.port.out.SongPersistencePort;
import org.example.remedy.application.user.exception.UserNotFoundException;
import org.example.remedy.application.user.port.out.UserPersistencePort;
import org.example.remedy.domain.dropping.Dropping;
import org.example.remedy.domain.dropping.DroppingType;
import org.example.remedy.domain.dropping.MusicDroppingPayload;
import org.example.remedy.domain.dropping.PlaylistDroppingPayload;
import org.example.remedy.domain.user.User;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DroppingServiceImpl implements DroppingService {

    private final DroppingPersistencePort droppingPersistencePort;
    private final SongPersistencePort songPersistencePort;
    private final UserPersistencePort userPersistencePort;

    @Override
    public DroppingSearchListResponse searchDroppings(double longitude, double latitude) {
        List<Dropping> allDroppings = droppingPersistencePort
                .findActiveDroppingsWithinRadius(longitude, latitude);

        List<DroppingResponse> droppings = allDroppings.stream()
                .map(this::convertToResponse)
                .toList();
        return DroppingSearchListResponse.of(droppings);
    }

    @Override
    public DroppingFindResponse getDropping(String droppingId) {
        Dropping dropping = droppingPersistencePort.findById(droppingId)
                .orElseThrow(() -> DroppingNotFoundException.EXCEPTION);
        String username = userPersistencePort.findByUserId(dropping.getUserId())
                .map(User::getUsername)
                .orElseThrow(() -> UserNotFoundException.EXCEPTION);

        if (dropping.getDroppingType() == DroppingType.MUSIC) {
            MusicDroppingPayload payload = (MusicDroppingPayload) dropping.getPayload();
            String albumImageUrl = songPersistencePort.findById(payload.getSongId())
                    .orElseThrow(() -> SongNotFoundException.EXCEPTION)
                    .getAlbumImagePath();
            return DroppingFindResponse.newInstance(dropping, payload.getSongId(), username, albumImageUrl);
        }

        return DroppingFindResponse.newInstance(dropping, null, username, null);
    }

    @Override
    public DroppingSearchListResponse getUserDroppings(Long userId) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        List<Dropping> allDroppings = droppingPersistencePort.findByUserId(userId, sort);

        List<DroppingResponse> droppings = allDroppings.stream()
                .map(this::convertToResponse)
                .toList();
        return DroppingSearchListResponse.of(droppings);
    }

    private DroppingResponse convertToResponse(Dropping dropping) {
        return switch (dropping.getDroppingType()) {
            case MUSIC -> createMusicResponse(dropping);
            case VOTE -> createVoteResponse(dropping);
            case PLAYLIST -> createPlaylistResponse(dropping);
        };
    }

    private MusicDroppingSearchResponse createMusicResponse(Dropping dropping) {
        MusicDroppingPayload payload = (MusicDroppingPayload) dropping.getPayload();
        String albumImageUrl = songPersistencePort.findById(payload.getSongId())
                .orElseThrow(() -> SongNotFoundException.EXCEPTION)
                .getAlbumImagePath();
        return MusicDroppingSearchResponse.from(dropping, albumImageUrl);
    }

    private VoteDroppingSearchResponse createVoteResponse(Dropping dropping) {
        return VoteDroppingSearchResponse.from(dropping);
    }

    private PlaylistDroppingSearchResponse createPlaylistResponse(Dropping dropping) {
        return PlaylistDroppingSearchResponse.from(dropping);
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
}