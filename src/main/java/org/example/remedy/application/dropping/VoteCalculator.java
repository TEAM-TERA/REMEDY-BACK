package org.example.remedy.application.dropping;

import org.example.remedy.application.dropping.dto.response.VoteOptionInfo;
import org.example.remedy.domain.dropping.VoteDroppingPayload;
import org.example.remedy.domain.song.Song;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class VoteCalculator {

    public static VoteCalculationResult calculate(
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

    public record VoteCalculationResult(
            List<VoteOptionInfo> options,
            int totalVotes,
            String userVotedSongId
    ) {
    }
}