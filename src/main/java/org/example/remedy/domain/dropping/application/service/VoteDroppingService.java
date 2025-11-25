package org.example.remedy.domain.dropping.application.service;

import lombok.RequiredArgsConstructor;
import org.example.remedy.domain.dropping.application.dto.request.DroppingCreateRequest;
import org.example.remedy.domain.dropping.application.dto.response.VoteDroppingResponse;
import org.example.remedy.domain.dropping.application.exception.DroppingNotFoundException;
import org.example.remedy.domain.dropping.application.mapper.DroppingMapper;
import org.example.remedy.domain.dropping.domain.Dropping;
import org.example.remedy.domain.dropping.domain.VoteDroppingPayload;
import org.example.remedy.domain.dropping.repository.DroppingRepository;
import org.example.remedy.domain.song.application.exception.SongNotFoundException;
import org.example.remedy.domain.song.repository.SongRepository;
import org.example.remedy.global.security.auth.AuthDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VoteDroppingService {

    private final DroppingRepository droppingRepository;
    private final SongRepository songRepository;

    @Transactional
    public void createVoteDropping(AuthDetails authDetails, DroppingCreateRequest request) {
        VoteDroppingPayload payload = DroppingMapper.toVoteDroppingPayload(request);
        Dropping dropping = DroppingMapper.toVoteDroppingEntity(authDetails, request, payload);

        droppingRepository.createDropping(dropping);
    }

    @Transactional
    public void vote(String droppingId, Long userId, String songId) {
        Dropping dropping = droppingRepository.findById(droppingId)
                .orElseThrow(() -> DroppingNotFoundException.EXCEPTION);
        dropping.vote(userId, songId);
        droppingRepository.save(dropping);
    }

    @Transactional
    public void cancelVote(String droppingId, Long userId) {
        Dropping dropping = droppingRepository.findById(droppingId)
                .orElseThrow(() -> DroppingNotFoundException.EXCEPTION);
        dropping.cancelVote(userId);
        droppingRepository.save(dropping);
    }

    public VoteDroppingResponse getVoteDropping(String droppingId, Long userId) {
        Dropping dropping = droppingRepository.findById(droppingId)
                .orElseThrow(() -> DroppingNotFoundException.EXCEPTION);
        return DroppingMapper.toVoteDroppingResponse(
                dropping,
                userId,
                songId -> songRepository.findById(songId)
                        .orElseThrow(() -> SongNotFoundException.EXCEPTION)
        );
    }
}
