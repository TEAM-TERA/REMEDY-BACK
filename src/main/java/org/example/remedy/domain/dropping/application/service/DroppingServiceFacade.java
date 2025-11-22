package org.example.remedy.domain.dropping.application.service;

import lombok.RequiredArgsConstructor;
import org.example.remedy.domain.dropping.application.dto.request.DroppingCreateRequest;
import org.example.remedy.domain.dropping.application.dto.request.PlaylistDroppingCreateRequest;
import org.example.remedy.domain.dropping.application.dto.request.VoteDroppingCreateRequest;
import org.example.remedy.domain.dropping.application.dto.response.DroppingFindResponse;
import org.example.remedy.domain.dropping.application.dto.response.DroppingSearchListResponse;
import org.example.remedy.domain.dropping.application.dto.response.PlaylistDroppingResponse;
import org.example.remedy.domain.dropping.application.dto.response.VoteDroppingResponse;
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
        musicDroppingService.createDropping(authDetails, request);
    }

    public DroppingSearchListResponse searchDroppings(double longitude, double latitude) {
        return droppingQueryService.searchDroppings(longitude, latitude);
    }

    public DroppingFindResponse getDropping(String droppingId) {
        return droppingQueryService.getDropping(droppingId);
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

    public void createVoteDropping(AuthDetails authDetails, VoteDroppingCreateRequest request) {
        voteDroppingService.createVoteDropping(authDetails, request);
    }

    public void vote(String droppingId, Long userId, String songId) {
        voteDroppingService.vote(droppingId, userId, songId);
    }

    public void cancelVote(String droppingId, Long userId) {
        voteDroppingService.cancelVote(droppingId, userId);
    }

    public VoteDroppingResponse getVoteDropping(String droppingId, Long userId) {
        return voteDroppingService.getVoteDropping(droppingId, userId);
    }

    public void createPlaylistDropping(AuthDetails authDetails, PlaylistDroppingCreateRequest request) {
        playlistDroppingService.createPlaylistDropping(authDetails, request);
    }

    public PlaylistDroppingResponse getPlaylistDropping(String droppingId, Long userId) {
        return playlistDroppingService.getPlaylistDropping(droppingId, userId);
    }
}