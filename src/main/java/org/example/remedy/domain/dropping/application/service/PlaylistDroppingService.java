package org.example.remedy.domain.dropping.application.service;

import lombok.RequiredArgsConstructor;
import org.example.remedy.domain.dropping.application.dto.response.PlaylistDroppingResponse;
import org.example.remedy.domain.dropping.application.exception.DroppingNotFoundException;
import org.example.remedy.domain.dropping.repository.DroppingRepository;
import org.example.remedy.domain.song.application.exception.SongNotFoundException;
import org.example.remedy.domain.song.repository.SongRepository;
import org.example.remedy.domain.dropping.domain.Dropping;
import org.example.remedy.domain.dropping.domain.DroppingType;
import org.example.remedy.domain.dropping.domain.PlaylistDroppingPayload;
import org.example.remedy.global.security.auth.AuthDetails;
import org.example.remedy.domain.dropping.application.dto.request.PlaylistDroppingCreateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PlaylistDroppingService {

    private final DroppingRepository droppingRepository;
    private final SongRepository songRepository;

    @Transactional
    public void createPlaylistDropping(AuthDetails authDetails, PlaylistDroppingCreateRequest request) {
        LocalDateTime now = LocalDateTime.now();

        PlaylistDroppingPayload payload = PlaylistDroppingPayload.builder()
                .playlistName(request.playlistName())
                .songIds(request.songIds())
                .build();

        Dropping dropping = new Dropping(
                DroppingType.PLAYLIST,
                payload,
                authDetails.getUserId(),
                request.content(),
                request.latitude(),
                request.longitude(),
                request.address(),
                now.plusDays(3),
                now,
                false
        );

        droppingRepository.createDropping(dropping);
    }

    public PlaylistDroppingResponse getPlaylistDropping(String droppingId, Long userId) {
        Dropping dropping = droppingRepository.findById(droppingId)
                .orElseThrow(() -> DroppingNotFoundException.EXCEPTION);
        return PlaylistDroppingResponse.from(
                dropping,
                songId -> songRepository.findById(songId)
                        .orElseThrow(() -> SongNotFoundException.EXCEPTION)
        );
    }
}