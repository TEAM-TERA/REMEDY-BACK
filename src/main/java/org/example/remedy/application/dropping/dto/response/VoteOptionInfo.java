package org.example.remedy.application.dropping.dto.response;

public record VoteOptionInfo(
        String songId,
        String albumImagePath,
        String title,
        String artist,
        int voteCount
) {
}
