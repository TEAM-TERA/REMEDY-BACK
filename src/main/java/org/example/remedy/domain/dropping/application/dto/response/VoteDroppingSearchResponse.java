package org.example.remedy.domain.dropping.application.dto.response;

import org.example.remedy.domain.dropping.domain.Dropping;
import org.example.remedy.domain.dropping.domain.DroppingType;
import org.example.remedy.domain.dropping.domain.VoteDroppingPayload;

import java.util.List;

public record VoteDroppingSearchResponse(
        DroppingType type,
        String droppingId,
        Long userId,
        String topic,
        List<String> options,
        Double latitude,
        Double longitude,
        String address
) implements DroppingResponse {

    public static VoteDroppingSearchResponse from(Dropping dropping) {
        VoteDroppingPayload payload = dropping.getVotePayload();

        return new VoteDroppingSearchResponse(
                DroppingType.VOTE,
                dropping.getDroppingId(),
                dropping.getUserId(),
                payload.getTopic(),
                List.copyOf(payload.getOptionVotes().keySet()),
                dropping.getLatitude(),
                dropping.getLongitude(),
                dropping.getAddress()
        );
    }
}