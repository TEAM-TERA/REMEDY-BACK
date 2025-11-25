package org.example.remedy.domain.dropping.application.mapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;
import org.example.remedy.domain.dropping.application.dto.request.DroppingCreateRequest;
import org.example.remedy.domain.dropping.application.dto.request.PlaylistDroppingCreateRequest;
import org.example.remedy.domain.dropping.application.dto.request.VoteDroppingCreateRequest;
import org.example.remedy.domain.dropping.application.dto.response.*;
import org.example.remedy.domain.dropping.application.util.VoteCalculator;
import org.example.remedy.domain.dropping.domain.Dropping;
import org.example.remedy.domain.dropping.domain.DroppingType;
import org.example.remedy.domain.dropping.domain.MusicDroppingPayload;
import org.example.remedy.domain.dropping.domain.PlaylistDroppingPayload;
import org.example.remedy.domain.dropping.domain.VoteDroppingPayload;
import org.example.remedy.domain.song.domain.Song;
import org.example.remedy.global.security.auth.AuthDetails;

public class DroppingMapper {

  public static DroppingSearchListResponse toDroppingSearchListResponse(
      List<DroppingResponse> droppings) {
    return new DroppingSearchListResponse(droppings);
  }

  public static DroppingFindResponse toDroppingFindResponse(
      Dropping dropping, String songId, String username, String albumImageUrl) {
    return new DroppingFindResponse(
        dropping.getDroppingId(),
        songId,
        dropping.getUserId(),
        username,
        dropping.getContent(),
        dropping.getExpiryDate(),
        dropping.getCreatedAt(),
        albumImageUrl);
  }

  public static MusicDroppingSearchResponse toMusicDroppingSearchResponse(
      Dropping dropping, Song song) {
    MusicDroppingPayload payload = (MusicDroppingPayload) dropping.getPayload();

    return new MusicDroppingSearchResponse(
        DroppingType.MUSIC,
        dropping.getDroppingId(),
        dropping.getUserId(),
        payload.getSongId(),
        song.getTitle(),
        song.getArtist(),
        dropping.getContent(),
        dropping.getLatitude(),
        dropping.getLongitude(),
        dropping.getAddress(),
        song.getAlbumImagePath());
  }

  public static VoteDroppingSearchResponse toVoteDroppingSearchResponse(Dropping dropping, String firstAlbumImageUrl) {
    VoteDroppingPayload payload = dropping.getVotePayload();

    return new VoteDroppingSearchResponse(
        DroppingType.VOTE,
        dropping.getDroppingId(),
        dropping.getUserId(),
        payload.getTopic(),
        List.copyOf(payload.getOptionVotes().keySet()),
        dropping.getContent(),
        dropping.getLatitude(),
        dropping.getLongitude(),
        dropping.getAddress(),
        firstAlbumImageUrl);
  }

  public static PlaylistDroppingSearchResponse toPlaylistDroppingSearchResponse(Dropping dropping, String firstAlbumImageUrl) {
    PlaylistDroppingPayload payload = (PlaylistDroppingPayload) dropping.getPayload();

    return new PlaylistDroppingSearchResponse(
        DroppingType.PLAYLIST,
        dropping.getDroppingId(),
        dropping.getUserId(),
        payload.getPlaylistName(),
        payload.getSongIds(),
        dropping.getContent(),
        dropping.getLatitude(),
        dropping.getLongitude(),
        dropping.getAddress(),
        firstAlbumImageUrl);
  }

  public static Dropping toEntity(
      AuthDetails authDetails, DroppingCreateRequest request, MusicDroppingPayload payload) {
    LocalDateTime now = LocalDateTime.now();
    return new Dropping(
        DroppingType.MUSIC,
        payload,
        authDetails.getUserId(),
        request.content(),
        request.latitude(),
        request.longitude(),
        request.address(),
        now.plusDays(3),
        now,
        false);
  }

  public static MusicDroppingPayload toPayload(DroppingCreateRequest request) {
    return MusicDroppingPayload.builder().songId(request.songId()).build();
  }

  public static VoteDroppingResponse toVoteDroppingResponse(
      Dropping dropping, Long currentUserId, Function<String, Song> songFinder) {
    VoteDroppingPayload payload = dropping.getVotePayload();

    VoteCalculator.VoteCalculationResult calc =
        VoteCalculator.calculate(payload, currentUserId, songFinder);

    return new VoteDroppingResponse(
        dropping.getDroppingId(),
        dropping.getUserId(),
        payload.getTopic(),
        calc.options(),
        dropping.getContent(),
        dropping.getLatitude(),
        dropping.getLongitude(),
        dropping.getAddress(),
        dropping.getExpiryDate(),
        dropping.getCreatedAt(),
        calc.totalVotes(),
        calc.userVotedSongId());
  }

  public static Dropping toVoteDroppingEntity(
      AuthDetails authDetails, VoteDroppingCreateRequest request, VoteDroppingPayload payload) {
    LocalDateTime now = LocalDateTime.now();
    return new Dropping(
        DroppingType.VOTE,
        payload,
        authDetails.getUserId(),
        request.content(),
        request.latitude(),
        request.longitude(),
        request.address(),
        now.plusDays(3),
        now,
        false);
  }

  public static VoteDroppingPayload toVoteDroppingPayload(VoteDroppingCreateRequest request) {
    LinkedHashMap<String, List<Long>> optionVotes = new LinkedHashMap<>();
    for (String songId : request.options()) {
      optionVotes.put(songId, new ArrayList<>());
    }

    return VoteDroppingPayload.builder().topic(request.topic()).optionVotes(optionVotes).build();
  }

  public static PlaylistDroppingResponse toPlaylistDroppingResponse(
      Dropping dropping, Function<String, Song> songFinder) {
    PlaylistDroppingPayload payload = (PlaylistDroppingPayload) dropping.getPayload();

    List<PlaylistDroppingResponse.SongInfo> songs =
        payload.getSongIds().stream()
            .map(songFinder)
            .map(
                song ->
                    new PlaylistDroppingResponse.SongInfo(
                        song.getId(), song.getTitle(), song.getArtist(), song.getAlbumImagePath()))
            .toList();

    return new PlaylistDroppingResponse(
        dropping.getDroppingId(),
        dropping.getUserId(),
        payload.getPlaylistName(),
        songs,
        dropping.getContent(),
        dropping.getLatitude(),
        dropping.getLongitude(),
        dropping.getAddress(),
        dropping.getExpiryDate(),
        dropping.getCreatedAt());
  }

  public static Dropping toPlaylistDroppingEntity(
      AuthDetails authDetails,
      PlaylistDroppingCreateRequest request,
      PlaylistDroppingPayload payload) {
    LocalDateTime now = LocalDateTime.now();
    return new Dropping(
        DroppingType.PLAYLIST,
        payload,
        authDetails.getUserId(),
        request.content(),
        request.latitude(),
        request.longitude(),
        request.address(),
        now.plusDays(3),
        now,
        false);
  }

  public static PlaylistDroppingPayload toPlaylistDroppingPayload(
      PlaylistDroppingCreateRequest request) {
    return PlaylistDroppingPayload.builder()
        .playlistName(request.playlistName())
        .songIds(request.songIds())
        .build();
  }
}
