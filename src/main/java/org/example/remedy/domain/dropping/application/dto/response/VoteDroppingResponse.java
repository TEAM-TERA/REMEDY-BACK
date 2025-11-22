package org.example.remedy.domain.dropping.application.dto.response;

import org.example.remedy.domain.dropping.application.util.VoteCalculator;
import org.example.remedy.domain.dropping.domain.Dropping;
import org.example.remedy.domain.dropping.domain.VoteDroppingPayload;
import org.example.remedy.domain.song.domain.Song;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

public record VoteDroppingResponse(
        String droppingId,
        Long userId,
        String topic,
        List<VoteOptionInfo> options,
        String content,
        Double latitude,
        Double longitude,
        String address,
        LocalDateTime expiryDate,
        LocalDateTime createdAt,
        int totalVotes,
        String userVotedOption
) {

}