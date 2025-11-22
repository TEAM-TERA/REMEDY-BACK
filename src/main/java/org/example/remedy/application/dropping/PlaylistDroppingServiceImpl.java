package org.example.remedy.application.dropping;

import lombok.RequiredArgsConstructor;
import org.example.remedy.application.dropping.dto.response.PlaylistDroppingResponse;
import org.example.remedy.application.dropping.exception.DroppingNotFoundException;
import org.example.remedy.application.dropping.port.in.PlaylistDroppingService;
import org.example.remedy.application.dropping.port.out.DroppingPersistencePort;
import org.example.remedy.application.song.exception.SongNotFoundException;
import org.example.remedy.application.song.port.out.SongPersistencePort;
import org.example.remedy.domain.dropping.Dropping;
import org.example.remedy.domain.dropping.DroppingType;
import org.example.remedy.domain.dropping.PlaylistDroppingPayload;
import org.example.remedy.global.security.auth.AuthDetails;
import org.example.remedy.presentation.dropping.dto.request.PlaylistDroppingCreateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PlaylistDroppingServiceImpl implements PlaylistDroppingService {

    private final DroppingPersistencePort droppingPersistencePort;
    private final SongPersistencePort songPersistencePort;

    @Override
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

        droppingPersistencePort.createDropping(dropping);
    }

    @Override
    public PlaylistDroppingResponse getPlaylistDropping(String droppingId, Long userId) {
        Dropping dropping = droppingPersistencePort.findById(droppingId)
                .orElseThrow(() -> DroppingNotFoundException.EXCEPTION);
        return PlaylistDroppingResponse.from(
                dropping,
                songId -> songPersistencePort.findById(songId)
                        .orElseThrow(() -> SongNotFoundException.EXCEPTION)
        );
    }
}