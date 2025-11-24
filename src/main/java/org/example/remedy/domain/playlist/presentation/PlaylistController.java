package org.example.remedy.domain.playlist.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.remedy.domain.playlist.application.dto.request.PlaylistCreateRequest;
import org.example.remedy.domain.playlist.application.dto.request.PlaylistSongAddRequest;
import org.example.remedy.domain.playlist.application.dto.request.PlaylistUpdateRequest;
import org.example.remedy.domain.playlist.application.dto.response.PlaylistDetailResponse;
import org.example.remedy.domain.playlist.application.dto.response.PlaylistListResponse;
import org.example.remedy.domain.playlist.application.service.PlaylistService;
import org.example.remedy.global.security.auth.AuthDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/playlists")
@RequiredArgsConstructor
public class PlaylistController {

    private final PlaylistService playlistService;

    @PostMapping
    public ResponseEntity<Void> createPlaylist(
            @AuthenticationPrincipal AuthDetails authDetails,
            @Valid @RequestBody PlaylistCreateRequest request) {
        playlistService.createPlaylist(authDetails.getUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{playlist-id}")
    public ResponseEntity<PlaylistDetailResponse> getPlaylist(
            @PathVariable(name = "playlist-id") String playlistId) {
        PlaylistDetailResponse response = playlistService.getPlaylist(playlistId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my")
    public ResponseEntity<PlaylistListResponse> getMyPlaylists(
            @AuthenticationPrincipal AuthDetails authDetails) {
        PlaylistListResponse response = playlistService.getMyPlaylists(authDetails.getUserId());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{playlist-id}")
    public ResponseEntity<Void> updatePlaylist(
            @PathVariable(name = "playlist-id") String playlistId,
            @AuthenticationPrincipal AuthDetails authDetails,
            @Valid @RequestBody PlaylistUpdateRequest request) {
        playlistService.updatePlaylist(playlistId, authDetails.getUserId(), request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{playlist-id}")
    public ResponseEntity<Void> deletePlaylist(
            @PathVariable(name = "playlist-id") String playlistId,
            @AuthenticationPrincipal AuthDetails authDetails) {
        playlistService.deletePlaylist(playlistId, authDetails.getUserId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{playlist-id}/songs")
    public ResponseEntity<Void> addSongToPlaylist(
            @PathVariable(name = "playlist-id") String playlistId,
            @AuthenticationPrincipal AuthDetails authDetails,
            @Valid @RequestBody PlaylistSongAddRequest request) {
        playlistService.addSongToPlaylist(playlistId, authDetails.getUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{playlist-id}/songs/{song-id}")
    public ResponseEntity<Void> removeSongFromPlaylist(
            @PathVariable(name = "playlist-id") String playlistId,
            @PathVariable(name = "song-id") String songId,
            @AuthenticationPrincipal AuthDetails authDetails) {
        playlistService.removeSongFromPlaylist(playlistId, songId, authDetails.getUserId());
        return ResponseEntity.noContent().build();
    }
}
