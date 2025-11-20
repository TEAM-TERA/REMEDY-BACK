package org.example.remedy.application.dropping;

import lombok.RequiredArgsConstructor;
import org.example.remedy.application.dropping.port.in.DroppingServiceFacade;
import org.example.remedy.application.dropping.port.in.MusicDroppingService;
import org.example.remedy.application.dropping.port.in.VoteDroppingService;
import org.example.remedy.application.dropping.port.in.DroppingService;
import org.example.remedy.presentation.dropping.dto.request.DroppingCreateRequest;
import org.example.remedy.presentation.dropping.dto.request.VoteDroppingCreateRequest;
import org.example.remedy.application.dropping.dto.response.DroppingFindResponse;
import org.example.remedy.application.dropping.dto.response.DroppingSearchListResponse;
import org.example.remedy.application.dropping.dto.response.VoteDroppingResponse;
import org.example.remedy.global.security.auth.AuthDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DroppingServiceFacadeImpl implements DroppingServiceFacade {

    private final MusicDroppingService musicDroppingService;
    private final VoteDroppingService voteDroppingService;
    private final DroppingService droppingQueryService;

    @Override
    public void createDropping(AuthDetails authDetails, DroppingCreateRequest request) {
        musicDroppingService.createDropping(authDetails, request);
    }

    @Override
    public DroppingSearchListResponse searchDroppings(double longitude, double latitude) {
        return droppingQueryService.searchDroppings(longitude, latitude);
    }

    @Override
    public DroppingFindResponse getDropping(String droppingId) {
        return droppingQueryService.getDropping(droppingId);
    }

    @Override
    public DroppingSearchListResponse getUserDroppings(Long userId) {
        return droppingQueryService.getUserDroppings(userId);
    }

    @Override
    public void deleteDropping(String droppingId, Long userId) {
        droppingQueryService.deleteDropping(droppingId, userId);
    }

    @Override
    public void cleanupExpiredDroppings() {
        droppingQueryService.cleanupExpiredDroppings();
    }

    @Override
    public void createVoteDropping(AuthDetails authDetails, VoteDroppingCreateRequest request) {
        voteDroppingService.createVoteDropping(authDetails, request);
    }

    @Override
    public void vote(String droppingId, Long userId, String songId) {
        voteDroppingService.vote(droppingId, userId, songId);
    }

    @Override
    public void cancelVote(String droppingId, Long userId) {
        voteDroppingService.cancelVote(droppingId, userId);
    }

    @Override
    public VoteDroppingResponse getVoteDropping(String droppingId, Long userId) {
        return voteDroppingService.getVoteDropping(droppingId, userId);
    }
}