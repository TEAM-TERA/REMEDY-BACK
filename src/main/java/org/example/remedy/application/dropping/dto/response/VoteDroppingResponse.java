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

        VoteCalculationResult calc = calculateVotes(payload, currentUserId);

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
                calc.userVotedOption()
        );
    }

    private static VoteCalculationResult calculateVotes(VoteDroppingPayload payload, Long currentUserId) {
        List<VoteOptionInfo> optionInfos = new ArrayList<>();
        int totalVotes = 0;
        String userVotedOption = null;

        for (Map.Entry<String, List<Long>> entry : payload.getOptionVotes().entrySet()) {
            String optionText = entry.getKey();
            List<Long> voters = entry.getValue();

            int voteCount = voters.size();
            totalVotes += voteCount;

            optionInfos.add(new VoteOptionInfo(optionText, voteCount));

            if (voters.contains(currentUserId)) {
                userVotedOption = optionText;
            }
        }

        return new VoteCalculationResult(optionInfos, totalVotes, userVotedOption);
    }

    private record VoteCalculationResult(
            List<VoteOptionInfo> options,
            int totalVotes,
            String userVotedOption
    ) {}

    public record VoteOptionInfo(
            String optionText,
            int voteCount
    ) { }
}