package org.example.remedy.domain.dropping.application.dto.response;

import org.example.remedy.domain.dropping.domain.DroppingType;

import java.util.List;

public record PlaylistDroppingSearchResponse(
        DroppingType type,
        String droppingId,
        Long userId,
        String playlistName,
        List<String> songIds,
        String content,
        Double latitude,
        Double longitude,
        String address,
        String firstAlbumImageUrl
) implements DroppingResponse {

}
