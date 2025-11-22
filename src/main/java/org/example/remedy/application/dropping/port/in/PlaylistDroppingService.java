package org.example.remedy.application.dropping.port.in;

import org.example.remedy.application.dropping.dto.response.PlaylistDroppingResponse;
import org.example.remedy.global.security.auth.AuthDetails;
import org.example.remedy.presentation.dropping.dto.request.PlaylistDroppingCreateRequest;

public interface PlaylistDroppingService {
    void createPlaylistDropping(AuthDetails authDetails, PlaylistDroppingCreateRequest request);

    PlaylistDroppingResponse getPlaylistDropping(String droppingId, Long userId);
}