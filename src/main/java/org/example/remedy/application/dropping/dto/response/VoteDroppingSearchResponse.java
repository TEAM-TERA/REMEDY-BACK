package org.example.remedy.application.dropping.dto.response;

import org.example.remedy.domain.dropping.Dropping;
import org.example.remedy.domain.dropping.DroppingType;
import org.example.remedy.domain.dropping.VoteDroppingPayload;

import java.util.List;

public record VoteDroppingSearchResponse(
        DroppingType type,
        String droppingId,
        Long userId,
        String topic,
        List<String> options,
        String songId,
        Double latitude,
        Double longitude,
        String address,
        String albumImageUrl
) implements DroppingResponse {

    public static VoteDroppingSearchResponse from(Dropping dropping, String albumImageUrl) {
        VoteDroppingPayload payload = dropping.getVotePayload();

        return new VoteDroppingSearchResponse(
                DroppingType.VOTE,
                dropping.getDroppingId(),
                dropping.getUserId(),
                payload.getTopic(),
                List.copyOf(payload.getOptionVotes().keySet()),
                dropping.getSongId(),
                dropping.getLatitude(),
                dropping.getLongitude(),
                dropping.getAddress(),
                albumImageUrl
        );
    }
}