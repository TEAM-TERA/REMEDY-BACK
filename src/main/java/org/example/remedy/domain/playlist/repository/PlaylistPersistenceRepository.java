package org.example.remedy.domain.playlist.repository;

import org.example.remedy.domain.playlist.domain.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaylistPersistenceRepository extends JpaRepository<Playlist, Long> {
    List<Playlist> findByUserUserId(Long userId);
}