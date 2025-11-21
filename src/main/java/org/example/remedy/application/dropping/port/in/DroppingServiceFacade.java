package org.example.remedy.application.dropping.port.in;

import org.example.remedy.application.dropping.dto.response.DroppingFindResponse;
import org.example.remedy.application.dropping.dto.response.DroppingSearchListResponse;
import org.example.remedy.application.dropping.dto.response.VoteDroppingResponse;
import org.example.remedy.global.security.auth.AuthDetails;
import org.example.remedy.presentation.dropping.dto.request.DroppingCreateRequest;
import org.example.remedy.presentation.dropping.dto.request.VoteDroppingCreateRequest;

public interface DroppingServiceFacade {

    void createDropping(AuthDetails authDetails, DroppingCreateRequest request);

    DroppingFindResponse getDropping(String droppingId);

    DroppingSearchListResponse searchDroppings(double longitude, double latitude);

    DroppingSearchListResponse getUserDroppings(Long userId);

    void deleteDropping(String droppingId, Long userId);

    void cleanupExpiredDroppings();

    void createVoteDropping(AuthDetails authDetails, VoteDroppingCreateRequest request);

    void vote(String droppingId, Long userId, String songId);

    void cancelVote(String droppingId, Long userId);

    VoteDroppingResponse getVoteDropping(String droppingId, Long userId);
}
