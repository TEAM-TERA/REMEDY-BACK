package org.example.remedy.domain.dropping.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.remedy.domain.dropping.application.dto.response.DroppingFindResponse;
import org.example.remedy.domain.dropping.application.dto.response.DroppingResponse;
import org.example.remedy.domain.dropping.application.dto.response.DroppingSearchListResponse;
import org.example.remedy.domain.dropping.application.dto.response.MusicDroppingSearchResponse;
import org.example.remedy.domain.dropping.application.dto.response.PlaylistDroppingSearchResponse;
import org.example.remedy.domain.dropping.application.dto.response.VoteDroppingSearchResponse;
import org.example.remedy.domain.dropping.application.exception.DroppingNotFoundException;
import org.example.remedy.domain.dropping.application.exception.EmptyPlaylistSongsException;
import org.example.remedy.domain.dropping.application.exception.EmptyVoteOptionsException;
import org.example.remedy.domain.dropping.application.exception.InvalidDroppingDeleteRequestException;
import org.example.remedy.domain.dropping.application.mapper.DroppingMapper;
import org.example.remedy.domain.dropping.repository.DroppingRepository;
import org.example.remedy.domain.song.application.exception.SongNotFoundException;
import org.example.remedy.domain.song.repository.SongRepository;
import org.example.remedy.domain.user.application.exception.UserNotFoundException;
import org.example.remedy.domain.user.repository.UserRepository;
import org.example.remedy.domain.dropping.domain.Dropping;
import org.example.remedy.domain.dropping.domain.DroppingType;
import org.example.remedy.domain.dropping.domain.MusicDroppingPayload;
import org.example.remedy.domain.dropping.domain.PlaylistDroppingPayload;
import org.example.remedy.domain.dropping.domain.VoteDroppingPayload;
import org.example.remedy.domain.song.domain.Song;
import org.example.remedy.domain.user.domain.User;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DroppingService {

    private final DroppingRepository droppingRepository;
    private final SongRepository songRepository;
    private final UserRepository userRepository;

    public DroppingSearchListResponse searchDroppings(double longitude, double latitude) {
        List<Dropping> allDroppings = droppingRepository
                .findActiveDroppingsWithinRadius(longitude, latitude);

        List<DroppingResponse> droppings = allDroppings.stream()
                .map(this::convertToResponse)
                .toList();
        return DroppingMapper.toDroppingSearchListResponse(droppings);
    }

    public DroppingFindResponse getDropping(String droppingId) {
        Dropping dropping = droppingRepository.findById(droppingId)
                .orElseThrow(() -> DroppingNotFoundException.EXCEPTION);
        String username = userRepository.findByUserId(dropping.getUserId())
                .map(User::getUsername)
                .orElseThrow(() -> UserNotFoundException.EXCEPTION);

        if (dropping.getDroppingType() == DroppingType.MUSIC) {
            MusicDroppingPayload payload = (MusicDroppingPayload) dropping.getPayload();
            String albumImageUrl = songRepository.findById(payload.getSongId())
                    .orElseThrow(() -> SongNotFoundException.EXCEPTION)
                    .getAlbumImagePath();
            return DroppingMapper.toDroppingFindResponse(dropping, payload.getSongId(), username, albumImageUrl);
        }

        return DroppingMapper.toDroppingFindResponse(dropping, null, username, null);
    }

    public DroppingSearchListResponse getUserDroppings(Long userId) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        List<Dropping> allDroppings = droppingRepository.findByUserId(userId, sort);

        List<DroppingResponse> droppings = allDroppings.stream()
                .map(this::convertToResponse)
                .toList();
        return DroppingMapper.toDroppingSearchListResponse(droppings);
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
        Song song = songRepository.findById(payload.getSongId())
                .orElseThrow(() -> SongNotFoundException.EXCEPTION);

        return DroppingMapper.toMusicDroppingSearchResponse(dropping, song);
    }

    private VoteDroppingSearchResponse createVoteResponse(Dropping dropping) {
        VoteDroppingPayload payload = dropping.getVotePayload();

        String firstAlbumImageUrl = payload.getOptionVotes().keySet().stream()
                .findFirst()
                .flatMap(songRepository::findById)
                .map(Song::getAlbumImagePath)
                .orElseThrow(()-> EmptyVoteOptionsException.EXCEPTION);

        return DroppingMapper.toVoteDroppingSearchResponse(dropping, firstAlbumImageUrl);
    }

    private PlaylistDroppingSearchResponse createPlaylistResponse(Dropping dropping) {
        PlaylistDroppingPayload payload = (PlaylistDroppingPayload) dropping.getPayload();

        String firstAlbumImageUrl = payload.getSongIds().stream()
                .findFirst()
                .flatMap(songRepository::findById)
                .map(Song::getAlbumImagePath)
                .orElseThrow(()-> EmptyPlaylistSongsException.EXCEPTION);

        return DroppingMapper.toPlaylistDroppingSearchResponse(dropping, firstAlbumImageUrl);
    }

    @Transactional
    public void deleteDropping(String droppingId, Long userId) {
        Dropping dropping = droppingRepository.findById(droppingId)
                .orElseThrow(() -> DroppingNotFoundException.EXCEPTION);

        if (!dropping.getUserId().equals(userId)) {
            throw new InvalidDroppingDeleteRequestException();
        }

        droppingRepository.softDelete(dropping);
    }

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void cleanupExpiredDroppings() {
        List<Dropping> expiredDroppings = droppingRepository.findExpiredAndNotDeletedDroppings(LocalDateTime.now());

        if (expiredDroppings.isEmpty()) return;

        expiredDroppings.forEach(Dropping::markAsDeleted);
        droppingRepository.saveAll(expiredDroppings);

        log.info("만료된 Dropping {}개 자동 soft delete 완료", expiredDroppings.size());
    }
}
