package org.example.remedy.domain.song.repository;

import org.example.remedy.domain.song.domain.Song;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SongPersistenceRepository extends ElasticsearchRepository<Song, String> {
    List<Song> findByTitleContainingOrArtistContaining(String title, String artist);
}