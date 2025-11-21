package org.example.remedy.application.dropping;

import lombok.RequiredArgsConstructor;
import org.example.remedy.application.dropping.dto.response.VoteDroppingResponse;
import org.example.remedy.application.dropping.exception.DroppingNotFoundException;
import org.example.remedy.application.dropping.port.in.VoteDroppingService;
import org.example.remedy.application.dropping.port.out.DroppingPersistencePort;
import org.example.remedy.application.song.exception.SongNotFoundException;
import org.example.remedy.application.song.port.out.SongPersistencePort;
import org.example.remedy.domain.dropping.Dropping;
import org.example.remedy.domain.dropping.DroppingType;
import org.example.remedy.domain.dropping.VoteDroppingPayload;
import org.example.remedy.domain.song.Song;
import org.example.remedy.global.security.auth.AuthDetails;
import org.example.remedy.presentation.dropping.dto.request.VoteDroppingCreateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VoteDroppingServiceImpl implements VoteDroppingService {

    private final DroppingPersistencePort droppingPersistencePort;
    private final SongPersistencePort songPersistencePort;

    @Override
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

        droppingPersistencePort.createDropping(dropping);
    }

    @Override
    @Transactional
    public void vote(String droppingId, Long userId, String songId) {
        Dropping dropping = droppingPersistencePort.findById(droppingId)
                .orElseThrow(() -> DroppingNotFoundException.EXCEPTION);
        dropping.vote(userId, songId);
        droppingPersistencePort.save(dropping);
    }

    @Override
    @Transactional
    public void cancelVote(String droppingId, Long userId) {
        Dropping dropping = droppingPersistencePort.findById(droppingId)
                .orElseThrow(() -> DroppingNotFoundException.EXCEPTION);
        dropping.cancelVote(userId);
        droppingPersistencePort.save(dropping);
    }

    @Override
    public VoteDroppingResponse getVoteDropping(String droppingId, Long userId) {
        Dropping dropping = droppingPersistencePort.findById(droppingId)
                .orElseThrow(() -> DroppingNotFoundException.EXCEPTION);
        return VoteDroppingResponse.from(
                dropping,
                userId,
                songId -> songPersistencePort.findById(songId)
                        .orElseThrow(() -> SongNotFoundException.EXCEPTION)
        );
    }
}