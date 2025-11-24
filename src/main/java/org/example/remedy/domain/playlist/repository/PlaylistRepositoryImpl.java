package org.example.remedy.domain.playlist.repository;

import lombok.RequiredArgsConstructor;
import org.example.remedy.domain.playlist.domain.Playlist;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class PlaylistRepositoryImpl implements PlaylistRepository {

    private final PlaylistPersistenceRepository playlistPersistenceRepository;

    @Override
    public Playlist save(Playlist playlist) {
        return playlistPersistenceRepository.save(playlist);
    }

    @Override
    public Optional<Playlist> findById(Long id) {
        return playlistPersistenceRepository.findById(id);
    }

    @Override
    public List<Playlist> findByUserId(Long userId) {
        return playlistPersistenceRepository.findByUserUserId(userId);
    }

    @Override
    public void delete(Playlist playlist) {
        playlistPersistenceRepository.delete(playlist);
    }
}