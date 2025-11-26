package org.example.remedy.domain.like.application.service;

import lombok.RequiredArgsConstructor;

import org.example.remedy.domain.dropping.application.exception.DroppingNotFoundException;
import org.example.remedy.domain.dropping.domain.PlaylistDroppingPayload;
import org.example.remedy.domain.dropping.domain.VoteDroppingPayload;
import org.example.remedy.domain.dropping.repository.DroppingRepository;
import org.example.remedy.domain.like.application.dto.response.LikeDroppingListResponse;
import org.example.remedy.domain.like.application.dto.response.MusicLikeDroppingResponse;
import org.example.remedy.domain.like.application.dto.response.PlaylistLikeDroppingResponse;
import org.example.remedy.domain.like.application.dto.response.VoteLikeDroppingResponse;
import org.example.remedy.domain.like.application.mapper.LikeMapper;
import org.example.remedy.domain.like.repository.LikeRepository;
import org.example.remedy.domain.like.application.event.LikeCreatedEvent;
import org.example.remedy.domain.song.repository.SongRepository;
import org.example.remedy.domain.dropping.domain.Dropping;
import org.example.remedy.domain.like.domain.Like;
import org.example.remedy.domain.user.domain.User;
import org.example.remedy.global.event.GlobalEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final DroppingRepository droppingRepository;
    private final SongRepository songRepository;
    private final GlobalEventPublisher eventPublisher;

    @Transactional
    public boolean toggleLike(User user, String droppingId) {

        Dropping dropping = droppingRepository.findById(droppingId)
                .orElseThrow(() -> DroppingNotFoundException.EXCEPTION);

        Optional<Like> existingLike = likeRepository.findByUserAndDroppingId(user, droppingId);

        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
            return false;
        } else {
            Like like = LikeMapper.toEntity(user, droppingId);
            likeRepository.save(like);

            publishLikeCreatedEvent(user, dropping, droppingId);

            return true;
        }

    }

    private void publishLikeCreatedEvent(User liker, Dropping dropping, String droppingId) {
        // 자기 자신의 Dropping에 좋아요를 누른 경우 알림을 보내지 않음
        if (liker.getUserId().equals(dropping.getUserId())) {
            return;
        }

        LikeCreatedEvent event = LikeCreatedEvent.builder()
                .likerUserId(liker.getUserId())
                .likerUsername(liker.getUsername())
                .droppingOwnerUserId(dropping.getUserId())
                .droppingId(droppingId)
                .build();

        // SSE를 통해 Dropping 소유자에게 좋아요 알림 발송
        eventPublisher.publish(dropping.getUserId(), "like-created", event);
    }

    @Transactional(readOnly = true)
    public long getLikeCountByUser(User user) {

        return likeRepository.countByUser(user);
    }

    @Transactional(readOnly = true)
    public long getLikeCountByDropping(String droppingId) {

        if (!droppingRepository.existsById(droppingId)) {
            throw DroppingNotFoundException.EXCEPTION;
        }

        return likeRepository.countByDroppingId(droppingId);
    }

    @Transactional(readOnly = true)
    public LikeDroppingListResponse getLikeDroppingsDetailByUser(User user) {
        List<Object> droppings = likeRepository.findByUser(user).stream()
                .map(Like::getDroppingId)
                .map(this::convertToLikeDroppingResponse)
                .flatMap(Optional::stream)
                .toList();

        return new LikeDroppingListResponse(droppings);
    }

    private Optional<Object> convertToLikeDroppingResponse(String droppingId) {
        return droppingRepository.findById(droppingId)
                .filter(Dropping::isActive)
                .flatMap(this::createLikeResponse);
    }

    private Optional<Object> createLikeResponse(Dropping dropping) {
        return switch (dropping.getDroppingType()) {
            case MUSIC -> createMusicLikeResponse(dropping).map(response -> (Object) response);
            case VOTE -> createVoteLikeResponse(dropping).map(response -> (Object) response);
            case PLAYLIST -> createPlaylistLikeResponse(dropping).map(response -> (Object) response);
        };
    }

    private Optional<MusicLikeDroppingResponse> createMusicLikeResponse(Dropping dropping) {
        return songRepository.findById(dropping.getSongId())
                .flatMap(song -> LikeMapper.toMusicLikeResponse(dropping, song));
    }

    private Optional<VoteLikeDroppingResponse> createVoteLikeResponse(Dropping dropping) {
        VoteDroppingPayload payload = dropping.getVotePayload();
        String firstSongId = payload.getOptionVotes().keySet().stream()
                .findFirst()
                .orElse(null);

        if (firstSongId == null) {
            return LikeMapper.toVoteLikeResponse(dropping, null);
        }

        return songRepository.findById(firstSongId)
                .flatMap(song -> LikeMapper.toVoteLikeResponse(dropping, song));
    }

    private Optional<PlaylistLikeDroppingResponse> createPlaylistLikeResponse(Dropping dropping) {
        PlaylistDroppingPayload payload = (PlaylistDroppingPayload) dropping.getPayload();
        String firstSongId = payload.getSongIds().stream()
                .findFirst()
                .orElse(null);

        if (firstSongId == null) {
            return LikeMapper.toPlaylistLikeResponse(dropping, null);
        }

        return songRepository.findById(firstSongId)
                .flatMap(song -> LikeMapper.toPlaylistLikeResponse(dropping, song));
    }
}
