package org.example.remedy.domain.dropping.application.service;

import lombok.RequiredArgsConstructor;
import org.example.remedy.domain.dropping.application.dto.request.DroppingCreateRequest;
import org.example.remedy.domain.dropping.application.dto.response.DroppingFindResponse;
import org.example.remedy.domain.dropping.application.dto.response.DroppingSearchListResponse;
import org.example.remedy.domain.dropping.application.dto.response.PlaylistDroppingResponse;
import org.example.remedy.domain.dropping.application.dto.response.VoteDroppingResponse;
import org.example.remedy.domain.dropping.application.exception.InvalidDroppingTypeException;
import org.example.remedy.domain.dropping.domain.Dropping;
import org.example.remedy.global.security.auth.AuthDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DroppingServiceFacade {

    private final MusicDroppingService musicDroppingService;
    private final PlaylistDroppingService playlistDroppingService;
    private final VoteDroppingService voteDroppingService;
    private final DroppingService droppingQueryService;

    public void createDropping(AuthDetails authDetails, DroppingCreateRequest request) {
        switch (request.type()) {
            case MUSIC -> musicDroppingService.createDropping(authDetails, request);
            case VOTE -> voteDroppingService.createVoteDropping(authDetails, request);
            case PLAYLIST -> playlistDroppingService.createPlaylistDropping(authDetails, request);
            default -> throw InvalidDroppingTypeException.EXCEPTION;
        }
    }

    public DroppingSearchListResponse searchDroppings(double longitude, double latitude) {
        return droppingQueryService.searchDroppings(longitude, latitude);
    }

    public Object getDropping(String droppingId, Long userId) {
        Dropping dropping = droppingQueryService.getDroppingEntity(droppingId);

        return switch (dropping.getDroppingType()) {
            case MUSIC -> musicDroppingService.getMusicDropping(droppingId);
            case VOTE -> voteDroppingService.getVoteDropping(droppingId, userId);
            case PLAYLIST -> playlistDroppingService.getPlaylistDropping(droppingId);
        };
    }

    public DroppingSearchListResponse getUserDroppings(Long userId) {
        return droppingQueryService.getUserDroppings(userId);
    }

    public void deleteDropping(String droppingId, Long userId) {
        droppingQueryService.deleteDropping(droppingId, userId);
    }

    public void cleanupExpiredDroppings() {
        droppingQueryService.cleanupExpiredDroppings();
    }

    public void vote(String droppingId, Long userId, String songId) {
        voteDroppingService.vote(droppingId, userId, songId);
    }

    public void cancelVote(String droppingId, Long userId) {
        voteDroppingService.cancelVote(droppingId, userId);
    }
}
