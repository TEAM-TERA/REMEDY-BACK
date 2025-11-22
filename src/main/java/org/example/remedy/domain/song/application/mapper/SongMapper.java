package org.example.remedy.domain.song.application.mapper;

import java.util.List;
import java.util.stream.Collectors;
import org.example.remedy.domain.song.application.dto.response.SongListResponse;
import org.example.remedy.domain.song.application.dto.response.SongResponse;
import org.example.remedy.domain.song.application.dto.response.SongSearchListResponse;
import org.example.remedy.domain.song.application.dto.response.SongSearchResponse;
import org.example.remedy.domain.song.domain.Song;

public class SongMapper {

  public static SongResponse toSongResponse(Song song) {
    return new SongResponse(
        song.getId(),
        song.getTitle(),
        song.getArtist(),
        song.getDuration(),
        song.getAlbumImagePath());
  }

  public static SongSearchResponse toSongSearchResponse(Song song) {
    return new SongSearchResponse(
        song.getId(), song.getTitle(), song.getArtist(), song.getAlbumImagePath());
  }

  public static SongSearchListResponse toSongSearchListResponse(List<Song> songs) {
    List<SongSearchResponse> songSearchResponses =
        songs.stream().map(SongMapper::toSongSearchResponse).collect(Collectors.toList());
    return new SongSearchListResponse(songSearchResponses);
  }

  public static SongListResponse toSongListResponse(List<Song> songs) {
    List<SongResponse> songResponses =
        songs.stream().map(SongMapper::toSongResponse).collect(Collectors.toList());
    return new SongListResponse(songResponses);
  }
}
