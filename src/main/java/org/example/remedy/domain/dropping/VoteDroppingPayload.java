package org.example.remedy.domain.dropping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoteDroppingPayload implements Payload {

    private String topic;

    private String songId;

    @Builder.Default
    private LinkedHashMap<String, List<Long>> optionVotes = new LinkedHashMap<>();

    public void addVote(Long userId, String optionText) {
        if (!optionVotes.containsKey(optionText)) {
            throw new IllegalArgumentException("존재하지 않는 투표 옵션입니다");
        }

        optionVotes.values().forEach(voters -> voters.remove(userId));

        optionVotes.get(optionText).add(userId);
    }
}
