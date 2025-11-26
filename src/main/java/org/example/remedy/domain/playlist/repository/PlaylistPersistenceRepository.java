package org.example.remedy.domain.playlist.repository;

import org.example.remedy.domain.playlist.domain.Playlist;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaylistPersistenceRepository extends MongoRepository<Playlist, String> {
    List<Playlist> findByUserId(Long userId);
}