package org.example.remedy.application.dropping.dto.response;

import org.example.remedy.domain.dropping.Dropping;
import org.example.remedy.domain.dropping.VoteDroppingPayload;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    public static VoteDroppingResponse from(Dropping dropping, Long currentUserId) {
        VoteDroppingPayload payload = dropping.getVotePayload();

        List<VoteOptionInfo> options = new ArrayList<>();
        String userVotedOption = null;
        int totalVotes = 0;

        for (Map.Entry<String, List<Long>> entry : payload.getOptionVotes().entrySet()) {
            String optionText = entry.getKey();
            List<Long> voters = entry.getValue();
            int voteCount = voters.size();

            options.add(new VoteOptionInfo(optionText, voteCount));
            totalVotes += voteCount;

            if (voters.contains(currentUserId)) {
                userVotedOption = optionText;
            }
        }

        return new VoteDroppingResponse(
                dropping.getDroppingId(),
                dropping.getUserId(),
                payload.getTopic(),
                options,
                dropping.getContent(),
                dropping.getLatitude(),
                dropping.getLongitude(),
                dropping.getAddress(),
                dropping.getExpiryDate(),
                dropping.getCreatedAt(),
                totalVotes,
                userVotedOption
        );
    }

    public record VoteOptionInfo(
            String optionText,
            int voteCount
    ) { }
}