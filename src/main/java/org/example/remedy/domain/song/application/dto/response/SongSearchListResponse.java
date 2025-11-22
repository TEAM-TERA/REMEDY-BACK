package org.example.remedy.domain.song.application.dto.response;

import org.example.remedy.domain.song.domain.Song;

import java.util.ArrayList;
import java.util.List;

public record SongSearchListResponse(
        List<SongSearchResponse> songSearchResponses
) {
    public static SongSearchListResponse newInstanceBySongList(List<Song> songs) {
        List<SongSearchResponse> songSearchResponses = new ArrayList<>();
        for(Song song : songs) {
            songSearchResponses.add(SongSearchResponse.newInstance(song));
        }
        return new SongSearchListResponse(songSearchResponses);
    }
}
