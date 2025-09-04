package org.example.remedy.application.song;

import org.example.remedy.interfaces.song.dto.request.SongCreateRequest;
import org.example.remedy.interfaces.song.dto.response.SongListResponse;
import org.example.remedy.interfaces.song.dto.response.SongResponse;
import org.example.remedy.interfaces.song.dto.response.SongSearchListResponse;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

public interface SongService {
    SongListResponse createSongs(SongCreateRequest request);

    SongResponse getSongById(String id);

    SongSearchListResponse searchSongs(String query);

    SongListResponse getAllSongs();

    ResponseEntity<Resource> streamSong(String title) throws IOException;
}
