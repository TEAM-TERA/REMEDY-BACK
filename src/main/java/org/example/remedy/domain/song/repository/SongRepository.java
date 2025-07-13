package org.example.remedy.domain.song.repository;

import org.example.remedy.domain.song.domain.Song;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SongRepository extends MongoRepository<Song, String> {
    @Query("{'title': {$regex: ?0, $options: 'i'}}")
    List<Song> findByTitleOrArtistContainingIgnoreCase(String cleanQuery);
}