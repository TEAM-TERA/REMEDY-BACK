package org.example.remedy.domain.playlist.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.remedy.domain.playlist.application.dto.request.PlaylistCreateRequest;
import org.example.remedy.domain.playlist.application.dto.request.PlaylistSongAddRequest;
import org.example.remedy.domain.playlist.application.dto.request.PlaylistUpdateRequest;
import org.example.remedy.domain.playlist.application.dto.response.PlaylistDetailResponse;
import org.example.remedy.domain.playlist.application.dto.response.PlaylistListResponse;
import org.example.remedy.domain.playlist.application.dto.response.PlaylistResponse;
import org.example.remedy.domain.playlist.application.exception.PlaylistAccessDeniedException;
import org.example.remedy.domain.playlist.application.exception.PlaylistNotFoundException;
import org.example.remedy.domain.playlist.application.exception.SongAlreadyInPlaylistException;
import org.example.remedy.domain.playlist.application.exception.SongNotInPlaylistException;
import org.example.remedy.domain.playlist.application.mapper.PlaylistMapper;
import org.example.remedy.domain.playlist.domain.Playlist;
import org.example.remedy.domain.playlist.repository.PlaylistRepository;
import org.example.remedy.domain.song.application.dto.response.SongResponse;
import org.example.remedy.domain.song.application.exception.SongNotFoundException;
import org.example.remedy.domain.song.application.mapper.SongMapper;
import org.example.remedy.domain.song.repository.SongRepository;
import org.example.remedy.domain.user.application.exception.UserNotFoundException;
import org.example.remedy.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final UserRepository userRepository;
    private final SongRepository songRepository;

    public void createPlaylist(Long userId, PlaylistCreateRequest request) {
        // User 존재 여부 검증
        userRepository.findByUserId(userId)
                .orElseThrow(() -> UserNotFoundException.EXCEPTION);

        Playlist playlist = PlaylistMapper.toEntity(request, userId);
        playlistRepository.save(playlist);
    }

    public PlaylistDetailResponse getPlaylist(String playlistId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> PlaylistNotFoundException.EXCEPTION);

        List<SongResponse> songs = playlist.getSongIdList().stream()
                .map(songId -> songRepository.findById(songId)
                        .map(SongMapper::toSongResponse)
                        .orElseThrow(() -> SongNotFoundException.EXCEPTION))
                .collect(Collectors.toList());

        return PlaylistMapper.toPlaylistDetailResponse(playlist, songs);
    }

    public PlaylistListResponse getMyPlaylists(Long userId) {
        List<Playlist> playlists = playlistRepository.findByUserId(userId);

        List<PlaylistResponse> playlistResponses = playlists.stream()
                .map(PlaylistMapper::toPlaylistResponse)
                .collect(Collectors.toList());

        return PlaylistMapper.toPlaylistListResponse(playlistResponses);
    }

    public void updatePlaylist(String playlistId, Long userId, PlaylistUpdateRequest request) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> PlaylistNotFoundException.EXCEPTION);

        validatePlaylistOwner(playlist, userId);

        playlist.update(request.name());
        playlistRepository.save(playlist);
    }

    public void deletePlaylist(String playlistId, Long userId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> PlaylistNotFoundException.EXCEPTION);

        validatePlaylistOwner(playlist, userId);

        playlistRepository.delete(playlist);
    }

    public void addSongToPlaylist(String playlistId, Long userId, PlaylistSongAddRequest request) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> PlaylistNotFoundException.EXCEPTION);

        validatePlaylistOwner(playlist, userId);

        songRepository.findById(request.songId())
                .orElseThrow(() -> SongNotFoundException.EXCEPTION);

        if (playlist.hasSong(request.songId())) {
            throw SongAlreadyInPlaylistException.EXCEPTION;
        }

        playlist.addSong(request.songId());
        playlistRepository.save(playlist);
    }

    public void removeSongFromPlaylist(String playlistId, String songId, Long userId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> PlaylistNotFoundException.EXCEPTION);

        validatePlaylistOwner(playlist, userId);

        if (!playlist.hasSong(songId)) {
            throw SongNotInPlaylistException.EXCEPTION;
        }

        playlist.removeSong(songId);
        playlistRepository.save(playlist);
    }

    private void validatePlaylistOwner(Playlist playlist, Long userId) {
        if (!playlist.getUserId().equals(userId)) {
            throw PlaylistAccessDeniedException.EXCEPTION;
        }
    }
}
