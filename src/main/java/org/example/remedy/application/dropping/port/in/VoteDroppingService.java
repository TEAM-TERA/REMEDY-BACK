package org.example.remedy.application.dropping.port.in;

import org.example.remedy.application.dropping.dto.response.VoteDroppingResponse;
import org.example.remedy.global.security.auth.AuthDetails;
import org.example.remedy.presentation.dropping.dto.request.VoteDroppingCreateRequest;

public interface VoteDroppingService {

    void createVoteDropping(AuthDetails authDetails, VoteDroppingCreateRequest request);

    void vote(String droppingId, Long userId, String songId);

    void cancelVote(String droppingId, Long userId);

    VoteDroppingResponse getVoteDropping(String droppingId, Long userId);
}