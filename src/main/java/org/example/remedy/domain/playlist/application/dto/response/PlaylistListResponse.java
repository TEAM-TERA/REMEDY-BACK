package org.example.remedy.domain.playlist.application.dto.response;

import java.util.List;

public record PlaylistListResponse(
        List<PlaylistResponse> playlists
) {
}