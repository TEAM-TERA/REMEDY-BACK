package org.example.remedy.infrastructure.persistence.song;

import org.example.remedy.domain.song.Song;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SongRepository extends ElasticsearchRepository<Song, String> {
    List<Song> findByTitleContainingOrArtistContaining(String title, String artist);
    Optional<Song> findByTitle(String title);
    Optional<Song> findByTitleAndArtist(String title, String artist);
}