package org.example.remedy.domain.playlist.application.dto.response;

import org.example.remedy.domain.song.application.dto.response.SongResponse;

import java.util.List;

public record PlaylistDetailResponse(
        Long id,
        String name,
        List<SongResponse> songs
) {
}