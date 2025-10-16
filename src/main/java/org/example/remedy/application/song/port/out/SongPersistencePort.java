package org.example.remedy.application.song.port.out;

import org.example.remedy.domain.song.Song;

import java.util.Optional;

public interface SongPersistencePort {
    Optional<Song> findById(String id);
    Iterable<Song> findAll();
    Optional<Song> findByTitle(String title);
    Optional<Song> findByTitleAndArtist(String title, String artist);
    Song save(Song song);
}
