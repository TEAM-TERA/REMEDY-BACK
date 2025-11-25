package org.example.remedy.domain.dropping.application.service;

import lombok.RequiredArgsConstructor;
import org.example.remedy.domain.dropping.application.dto.request.DroppingCreateRequest;
import org.example.remedy.domain.dropping.application.dto.response.PlaylistDroppingResponse;
import org.example.remedy.domain.dropping.application.dto.response.PlaylistDroppingSearchResponse;
import org.example.remedy.domain.dropping.application.exception.EmptyPlaylistSongsException;
import org.example.remedy.domain.song.domain.Song;
import org.example.remedy.domain.dropping.application.exception.DroppingNotFoundException;
import org.example.remedy.domain.dropping.application.mapper.DroppingMapper;
import org.example.remedy.domain.dropping.repository.DroppingRepository;
import org.example.remedy.domain.song.application.exception.SongNotFoundException;
import org.example.remedy.domain.song.repository.SongRepository;
import org.example.remedy.domain.dropping.domain.Dropping;
import org.example.remedy.domain.dropping.domain.PlaylistDroppingPayload;
import org.example.remedy.global.security.auth.AuthDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlaylistDroppingService {

    private final DroppingRepository droppingRepository;
    private final SongRepository songRepository;

    @Transactional
    public void createPlaylistDropping(AuthDetails authDetails, DroppingCreateRequest request) {
        PlaylistDroppingPayload payload = DroppingMapper.toPlaylistDroppingPayload(request);
        Dropping dropping = DroppingMapper.toPlaylistDroppingEntity(authDetails, request, payload);

        droppingRepository.createDropping(dropping);
    }

    public PlaylistDroppingResponse getPlaylistDropping(String droppingId) {
        Dropping dropping = droppingRepository.findById(droppingId)
                .orElseThrow(() -> DroppingNotFoundException.EXCEPTION);
        return DroppingMapper.toPlaylistDroppingResponse(
                dropping,
                songId -> songRepository.findById(songId)
                        .orElseThrow(() -> SongNotFoundException.EXCEPTION)
        );
    }

    public PlaylistDroppingSearchResponse createPlaylistSearchResponse(Dropping dropping) {
        PlaylistDroppingPayload payload = (PlaylistDroppingPayload) dropping.getPayload();

        String firstAlbumImageUrl = payload.getSongIds().stream()
                .findFirst()
                .flatMap(songRepository::findById)
                .map(Song::getAlbumImagePath)
                .orElseThrow(() -> EmptyPlaylistSongsException.EXCEPTION);

        return DroppingMapper.toPlaylistDroppingSearchResponse(dropping, firstAlbumImageUrl);
    }
}
