package org.example.remedy.domain.song.repository;

import org.example.remedy.domain.song.domain.Song;

import java.util.List;
import java.util.Optional;

public interface SongRepository {
    Optional<Song> findById(String id);
    Iterable<Song> findAll();
    Song save(Song song);
    void deleteById(String id);
    List<Song> searchSongs(String query);
}
