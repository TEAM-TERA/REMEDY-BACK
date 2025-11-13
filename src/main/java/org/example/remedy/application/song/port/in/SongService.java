package org.example.remedy.application.song.port.in;

import org.example.remedy.application.song.dto.response.SongListResponse;
import org.example.remedy.application.song.dto.response.SongResponse;
import org.example.remedy.application.song.dto.response.SongSearchListResponse;

public interface SongService {
    SongResponse getSongById(String id);

    SongSearchListResponse searchSongs(String query);

    SongListResponse getAllSongs();

    void deleteSong(String id);
}
