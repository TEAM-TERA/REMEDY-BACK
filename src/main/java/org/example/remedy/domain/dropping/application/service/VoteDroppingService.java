package org.example.remedy.domain.dropping.application.service;

import lombok.RequiredArgsConstructor;
import org.example.remedy.domain.dropping.application.dto.response.VoteDroppingResponse;
import org.example.remedy.domain.dropping.application.exception.DroppingNotFoundException;
import org.example.remedy.domain.dropping.repository.DroppingRepository;
import org.example.remedy.domain.song.application.exception.SongNotFoundException;
import org.example.remedy.domain.song.repository.SongRepository;
import org.example.remedy.domain.dropping.domain.Dropping;
import org.example.remedy.domain.dropping.domain.DroppingType;
import org.example.remedy.domain.dropping.domain.VoteDroppingPayload;
import org.example.remedy.global.security.auth.AuthDetails;
import org.example.remedy.domain.dropping.application.dto.request.VoteDroppingCreateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VoteDroppingService {

    private final DroppingRepository droppingRepository;
    private final SongRepository songRepository;

    @Transactional
    public void createVoteDropping(AuthDetails authDetails, VoteDroppingCreateRequest request) {
        LocalDateTime now = LocalDateTime.now();

        LinkedHashMap<String, List<Long>> optionVotes = new LinkedHashMap<>();
        for (String songId : request.options()) {
            optionVotes.put(songId, new ArrayList<>());
        }

        VoteDroppingPayload payload = VoteDroppingPayload.builder()
                .topic(request.topic())
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
        return VoteDroppingResponse.from(
                dropping,
                userId,
                songId -> songRepository.findById(songId)
                        .orElseThrow(() -> SongNotFoundException.EXCEPTION)
        );
    }
}