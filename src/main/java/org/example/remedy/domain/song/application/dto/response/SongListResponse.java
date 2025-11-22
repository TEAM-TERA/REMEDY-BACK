package org.example.remedy.domain.song.application.dto.response;

import org.example.remedy.domain.song.domain.Song;

import java.util.ArrayList;
import java.util.List;

public record SongListResponse(
    List<SongResponse> songResponses
) {
    public static SongListResponse newInstanceBySongs(List<Song> songs) {
        List<SongResponse> songResponses = new ArrayList<>();

        // Song List 반복하며 SongResponse List로 변환
        for(Song song : songs) {
            songResponses.add(SongResponse.newInstance(song));
        }

        // SongListResponse로 변환
        return new SongListResponse(songResponses);
    }
}