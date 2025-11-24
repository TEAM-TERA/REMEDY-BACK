package org.example.remedy.domain.playlist.repository;

import org.example.remedy.domain.playlist.domain.Playlist;

import java.util.List;
import java.util.Optional;

public interface PlaylistRepository {
    Playlist save(Playlist playlist);

    Optional<Playlist> findById(Long id);

    List<Playlist> findByUserId(Long userId);

    void delete(Playlist playlist);
}