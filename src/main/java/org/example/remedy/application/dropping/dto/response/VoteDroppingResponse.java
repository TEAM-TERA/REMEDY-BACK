package org.example.remedy.application.dropping.dto.response;

import org.example.remedy.domain.dropping.Dropping;
import org.example.remedy.domain.dropping.VoteDroppingPayload;
import org.example.remedy.domain.song.Song;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

        VoteCalculationResult calc = calculateVotes(payload, currentUserId, songFinder);

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

    private static VoteCalculationResult calculateVotes(
            VoteDroppingPayload payload,
            Long currentUserId,
            Function<String, Song> songFinder
    ) {
        List<VoteOptionInfo> optionInfos = new ArrayList<>();
        int totalVotes = 0;
        String userVotedSongId = null;

        for (Map.Entry<String, List<Long>> entry : payload.getOptionVotes().entrySet()) {
            String songId = entry.getKey();
            List<Long> voters = entry.getValue();

            Song song = songFinder.apply(songId);
            int voteCount = voters.size();
            totalVotes += voteCount;

            optionInfos.add(new VoteOptionInfo(
                    songId,
                    song.getAlbumImagePath(),
                    song.getTitle(),
                    song.getArtist(),
                    voteCount
            ));

            if (voters.contains(currentUserId)) {
                userVotedSongId = songId;
            }
        }

        return new VoteCalculationResult(optionInfos, totalVotes, userVotedSongId);
    }

    private record VoteCalculationResult(
            List<VoteOptionInfo> options,
            int totalVotes,
            String userVotedSongId
    ) {}

    public record VoteOptionInfo(
            String songId,
            String albumImagePath,
            String title,
            String artist,
            int voteCount
    ) { }
}