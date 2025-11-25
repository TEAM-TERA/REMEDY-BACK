package org.example.remedy.domain.dropping.application.dto.response;

public record VoteOptionInfo(
        String songId,
        String albumImagePath,
        String title,
        String artist,
        int voteCount
) {
}
