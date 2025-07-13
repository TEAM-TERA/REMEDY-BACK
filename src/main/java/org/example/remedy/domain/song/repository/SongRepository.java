package org.example.remedy.domain.song.repository;

import org.example.remedy.domain.song.domain.Song;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SongRepository extends MongoRepository<Song, String> {
}