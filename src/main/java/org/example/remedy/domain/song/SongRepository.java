package org.example.remedy.domain.song;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SongRepository extends ElasticsearchRepository<Song, String> {
    List<Song> findByTitleContainingOrArtistContaining(String title, String artist);
    Optional<Song> findByTitle(String title);
}