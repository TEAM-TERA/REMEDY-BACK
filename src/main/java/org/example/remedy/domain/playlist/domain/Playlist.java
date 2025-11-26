package org.example.remedy.domain.playlist.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "playlists")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Playlist {
    @Id
    private String id;

    private String name;

    private List<String> songIds;

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
