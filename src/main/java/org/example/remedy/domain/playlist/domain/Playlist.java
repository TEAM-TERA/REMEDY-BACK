package org.example.remedy.domain.playlist.domain;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "playlists")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Playlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "playlist_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Type(JsonBinaryType.class)
    @Column(name = "song_ids", columnDefinition = "jsonb")
    private List<String> songIds;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    public Playlist(String name, Long userId) {
        this.name = name;
        this.userId = userId;
        this.songIds = new ArrayList<>();
    }

    public void update(String name) {
        if (name != null && !name.isBlank()) {
            this.name = name;
        }
    }

    public void addSong(String songId) {
        if (hasSong(songId)) return;
        this.songIds.add(songId);
    }

    public void addSongs(List<String> songIds) {
        songIds.stream()
                .filter(songId -> !hasSong(songId))
                .forEach(this.songIds::add);
    }

    public void removeSong(String songId) {
        this.songIds.remove(songId);
    }

    public boolean hasSong(String songId) {
        return this.songIds.contains(songId);
    }

    public List<String> getSongIdList() {
        return new ArrayList<>(this.songIds);
    }
}
