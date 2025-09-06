package org.example.remedy.application.song.port.in;

import org.example.remedy.presentation.song.dto.request.SongCreateRequest;
import org.example.remedy.application.song.dto.response.SongListResponse;
import org.example.remedy.application.song.dto.response.SongResponse;
import org.example.remedy.application.song.dto.response.SongSearchListResponse;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

public interface SongService {
    SongResponse getSongById(String id);

    SongSearchListResponse searchSongs(String query);

    SongListResponse getAllSongs();

    ResponseEntity<Resource> streamSong(String title) throws IOException;
}
