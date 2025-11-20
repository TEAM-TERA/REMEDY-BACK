package org.example.remedy.application.dropping.dto.response;

import org.example.remedy.application.dropping.util.VoteCalculator;
import org.example.remedy.domain.dropping.Dropping;
import org.example.remedy.domain.dropping.VoteDroppingPayload;
import org.example.remedy.domain.song.Song;

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

    public static VoteDroppingResponse from(
            Dropping dropping,
            Long currentUserId,
            Function<String, Song> songFinder
    ) {
        VoteDroppingPayload payload = dropping.getVotePayload();

        VoteCalculator.VoteCalculationResult calc = VoteCalculator.calculate(payload, currentUserId, songFinder);

        return new VoteDroppingResponse(
                dropping.getDroppingId(),
                dropping.getUserId(),
                payload.getTopic(),
                calc.options(),
                dropping.getContent(),
                dropping.getLatitude(),
                dropping.getLongitude(),
                dropping.getAddress(),
                dropping.getExpiryDate(),
                dropping.getCreatedAt(),
                calc.totalVotes(),
                calc.userVotedSongId()
        );
    }
}