package org.example.remedy.domain.like.application.mapper;

import java.util.Optional;
import org.example.remedy.domain.dropping.domain.Dropping;
import org.example.remedy.domain.dropping.domain.PlaylistDroppingPayload;
import org.example.remedy.domain.dropping.domain.VoteDroppingPayload;
import org.example.remedy.domain.like.application.dto.response.MusicLikeDroppingResponse;
import org.example.remedy.domain.like.application.dto.response.PlaylistLikeDroppingResponse;
import org.example.remedy.domain.like.application.dto.response.VoteLikeDroppingResponse;
import org.example.remedy.domain.like.domain.Like;
import org.example.remedy.domain.song.domain.Song;
import org.example.remedy.domain.user.domain.User;

public class LikeMapper {
  public static Like toEntity(User user, String droppingId) {
    return new Like(user, droppingId);
  }

  public static Optional<MusicLikeDroppingResponse> toMusicLikeResponse(Dropping dropping, Song song) {
    return Optional.of(
        new MusicLikeDroppingResponse(
            dropping.getDroppingId(),
            dropping.getDroppingType(),
            song.getTitle(),
            song.getArtist(),
            song.getAlbumImagePath(),
            dropping.getAddress()));
  }

  public static Optional<VoteLikeDroppingResponse> toVoteLikeResponse(Dropping dropping) {
    VoteDroppingPayload payload = dropping.getVotePayload();
    return Optional.of(
        new VoteLikeDroppingResponse(
            dropping.getDroppingId(),
            dropping.getDroppingType(),
            payload.getTopic(),
            dropping.getAddress()));
  }

  public static Optional<PlaylistLikeDroppingResponse> toPlaylistLikeResponse(Dropping dropping) {
    PlaylistDroppingPayload payload = (PlaylistDroppingPayload) dropping.getPayload();
    return Optional.of(
        new PlaylistLikeDroppingResponse(
            dropping.getDroppingId(),
            dropping.getDroppingType(),
            payload.getPlaylistName(),
            dropping.getAddress()));
  }
}
