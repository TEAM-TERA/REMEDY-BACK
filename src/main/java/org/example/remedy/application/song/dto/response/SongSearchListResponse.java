package org.example.remedy.application.song.dto.response;

import org.example.remedy.domain.song.Song;

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
