package org.example.remedy.domain.playlist.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.remedy.domain.user.domain.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "playlists")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Playlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String songIds;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Playlist(String name, User user) {
        this.name = name;
        this.user = user;
        this.songIds = "";
    }

    public void update(String name) {
        if (name != null && !name.isBlank()) {
            this.name = name;
        }
    }

    private List<String> getSongList() {
        if (songIds == null || songIds.isEmpty()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(songIds.split(",")));
    }

    public void addSong(String songId) {
        if (hasSong(songId)) return;

        List<String> songs = getSongList();
        songs.add(songId);
        this.songIds = String.join(",", songs);
    }

    public void removeSong(String songId) {
        List<String> songs = getSongList();
        songs.remove(songId);
        this.songIds = String.join(",", songs);
    }

    public boolean hasSong(String songId) {
        return songIds != null && songIds.contains(songId);
    }

    public List<String> getSongIdList() {
        return getSongList();
    }
}
