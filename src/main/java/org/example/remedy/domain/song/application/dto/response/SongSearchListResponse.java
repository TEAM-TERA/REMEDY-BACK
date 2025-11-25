package org.example.remedy.domain.song.application.dto.response;

import java.util.List;

public record SongSearchListResponse(
        List<SongSearchResponse> songSearchResponses
) {
}
