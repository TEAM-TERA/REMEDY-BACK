package org.example.remedy.domain.like.application.service;

import lombok.RequiredArgsConstructor;

import org.example.remedy.domain.dropping.application.exception.DroppingNotFoundException;
import org.example.remedy.domain.dropping.repository.DroppingRepository;
import org.example.remedy.domain.like.application.dto.response.LikeDroppingResponse;
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
    public List<LikeDroppingResponse> getLikeDroppingsDetailByUser(User user) {
        return likeRepository.findByUser(user).stream()
                .map(like -> convertToLikeDroppingResponse(like.getDroppingId()))
                .flatMap(Optional::stream)
                .toList();
    }

    private Optional<LikeDroppingResponse> convertToLikeDroppingResponse(String droppingId) {
        return droppingRepository.findById(droppingId)
                .filter(Dropping::isActive)
                .flatMap(dropping -> switch (dropping.getDroppingType()) {
                    case MUSIC -> createMusicLikeResponse(dropping);
                    case VOTE -> LikeMapper.toVoteLikeResponse(dropping);
                    case PLAYLIST -> LikeMapper.toPlaylistLikeResponse(dropping);
                });
    }

    private Optional<LikeDroppingResponse> createMusicLikeResponse(Dropping dropping) {
        String songId = dropping.getSongId();
        return songRepository.findById(songId)
                .flatMap(song -> LikeMapper.toMusicLikeResponse(dropping, song));
    }
}
