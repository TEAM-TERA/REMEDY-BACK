package org.example.remedy.domain.dropping.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.remedy.domain.dropping.application.exception.InvalidVoteOptionException;

import java.util.LinkedHashMap;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoteDroppingPayload implements Payload {

    private String topic;

    @Builder.Default
    private LinkedHashMap<String, List<Long>> optionVotes = new LinkedHashMap<>();

    public void addVote(Long userId, String songId) {
        if (!optionVotes.containsKey(songId)) {
            throw InvalidVoteOptionException.EXCEPTION;
        }

        optionVotes.values().forEach(voters -> voters.remove(userId));

        optionVotes.get(songId).add(userId);
    }

    public void removeVote(Long userId) {
        optionVotes.values().forEach(voters -> voters.remove(userId));
    }
}
