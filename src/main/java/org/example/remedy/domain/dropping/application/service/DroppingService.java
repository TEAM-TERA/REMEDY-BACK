package org.example.remedy.domain.dropping.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.remedy.domain.dropping.application.dto.response.DroppingOwnershipResponse;
import org.example.remedy.domain.dropping.application.dto.response.DroppingResponse;
import org.example.remedy.domain.dropping.application.dto.response.DroppingSearchListResponse;
import org.example.remedy.domain.dropping.application.exception.DroppingNotFoundException;
import org.example.remedy.domain.dropping.application.exception.InvalidDroppingDeleteRequestException;
import org.example.remedy.domain.dropping.application.mapper.DroppingMapper;
import org.example.remedy.domain.dropping.repository.DroppingRepository;
import org.example.remedy.domain.dropping.domain.Dropping;
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
    private final MusicDroppingService musicDroppingService;
    private final VoteDroppingService voteDroppingService;
    private final PlaylistDroppingService playlistDroppingService;

    public DroppingSearchListResponse searchDroppings(double longitude, double latitude) {
        List<Dropping> allDroppings = droppingRepository
                .findActiveDroppingsWithinRadius(longitude, latitude);

        List<DroppingResponse> droppings = allDroppings.stream()
                .map(this::convertToResponse)
                .toList();
        return DroppingMapper.toDroppingSearchListResponse(droppings);
    }

    public Dropping getDroppingEntity(String droppingId) {
        return droppingRepository.findById(droppingId)
                .orElseThrow(() -> DroppingNotFoundException.EXCEPTION);
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
            case MUSIC -> musicDroppingService.createMusicSearchResponse(dropping);
            case VOTE -> voteDroppingService.createVoteSearchResponse(dropping);
            case PLAYLIST -> playlistDroppingService.createPlaylistSearchResponse(dropping);
        };
    }

    @Transactional
    public void deleteDropping(String droppingId, Long userId) {
        Dropping dropping = getDroppingEntity(droppingId);

        if (!dropping.getUserId().equals(userId)) {
            throw new InvalidDroppingDeleteRequestException();
        }

        droppingRepository.softDelete(dropping);
    }

    public DroppingOwnershipResponse checkOwnership(String droppingId, Long userId) {
        Dropping dropping = getDroppingEntity(droppingId);
        boolean isOwner = dropping.getUserId().equals(userId);
        return new DroppingOwnershipResponse(isOwner);
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
