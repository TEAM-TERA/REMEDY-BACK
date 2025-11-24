package org.example.remedy.domain.playlist.application.mapper;

import org.example.remedy.domain.playlist.application.dto.request.PlaylistCreateRequest;
import org.example.remedy.domain.playlist.application.dto.response.PlaylistDetailResponse;
import org.example.remedy.domain.playlist.application.dto.response.PlaylistListResponse;
import org.example.remedy.domain.playlist.application.dto.response.PlaylistResponse;
import org.example.remedy.domain.playlist.domain.Playlist;
import org.example.remedy.domain.song.application.dto.response.SongResponse;

import java.util.List;

public class PlaylistMapper {

    public static Playlist toEntity(PlaylistCreateRequest request, Long userId) {
        return new Playlist(request.name(), userId);
    }

    public static PlaylistResponse toPlaylistResponse(Playlist playlist) {
        return new PlaylistResponse(
                playlist.getId(),
                playlist.getName()
        );
    }

    public static PlaylistListResponse toPlaylistListResponse(List<PlaylistResponse> playlists) {
        return new PlaylistListResponse(playlists);
    }

    public static PlaylistDetailResponse toPlaylistDetailResponse(Playlist playlist, List<SongResponse> songs) {
        return new PlaylistDetailResponse(
                playlist.getId(),
                playlist.getName(),
                songs
        );
    }
}
