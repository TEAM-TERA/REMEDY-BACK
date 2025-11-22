package org.example.remedy.domain.dropping.application.dto.response;

import java.time.LocalDateTime;
import java.util.List;

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
