package org.example.remedy.infrastructure.persistence.song;

import lombok.RequiredArgsConstructor;
import org.example.remedy.application.song.port.out.SongPersistencePort;
import org.example.remedy.domain.song.Song;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ElasticsearchSongAdapter implements SongPersistencePort {

    private final SongRepository songRepository;

    @Override
    public Optional<Song> findById(String id) {
        return songRepository.findById(id);
    }

    @Override
    public Iterable<Song> findAll() {
        return songRepository.findAll();
    }

    @Override
    public Optional<Song> findByTitle(String title) {
        return songRepository.findByTitle(title);
    }

    @Override
    public Song save(Song song) {
        return songRepository.save(song);
    }
}
