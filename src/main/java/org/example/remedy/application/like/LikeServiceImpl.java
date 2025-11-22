package org.example.remedy.application.like;

import lombok.RequiredArgsConstructor;

import org.example.remedy.application.dropping.exception.DroppingNotFoundException;
import org.example.remedy.application.dropping.port.out.DroppingPersistencePort;
import org.example.remedy.application.like.dto.response.LikeDroppingResponse;
import org.example.remedy.application.like.port.in.LikeService;
import org.example.remedy.application.like.port.out.LikePersistencePort;
import org.example.remedy.application.like.event.LikeCreatedEvent;
import org.example.remedy.application.song.port.out.SongPersistencePort;
import org.example.remedy.domain.dropping.Dropping;
import org.example.remedy.domain.dropping.PlaylistDroppingPayload;
import org.example.remedy.domain.dropping.VoteDroppingPayload;
import org.example.remedy.domain.like.Like;
import org.example.remedy.domain.user.User;
import org.example.remedy.global.event.GlobalEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {
    private final LikePersistencePort likePersistencePort;
    private final DroppingPersistencePort droppingPersistencePort;
    private final SongPersistencePort songPersistencePort;
    private final GlobalEventPublisher eventPublisher;

    @Override
    @Transactional
    public boolean toggleLike(User user, String droppingId) {

        Dropping dropping = droppingPersistencePort.findById(droppingId)
                .orElseThrow(() -> DroppingNotFoundException.EXCEPTION);

        Optional<Like> existingLike = likePersistencePort.findByUserAndDroppingId(user, droppingId);

        if (existingLike.isPresent()) {
            likePersistencePort.delete(existingLike.get());
            return false;
        } else {
            Like like = new Like(user, droppingId);
            likePersistencePort.save(like);
            
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

    @Override
    @Transactional(readOnly = true)
    public long getLikeCountByUser(User user) {

        return likePersistencePort.countByUser(user);
    }

    @Override
    @Transactional(readOnly = true)
    public long getLikeCountByDropping(String droppingId) {

        if (!droppingPersistencePort.existsById(droppingId)) {
            throw DroppingNotFoundException.EXCEPTION;
        }

        return likePersistencePort.countByDroppingId(droppingId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LikeDroppingResponse> getLikeDroppingsDetailByUser(User user) {
        return likePersistencePort.findByUser(user).stream()
                .map(like -> convertToLikeDroppingResponse(like.getDroppingId()))
                .flatMap(Optional::stream)
                .toList();
    }

    private Optional<LikeDroppingResponse> convertToLikeDroppingResponse(String droppingId) {
        return droppingPersistencePort.findById(droppingId)
                .filter(Dropping::isActive)
                .flatMap(dropping -> switch (dropping.getDroppingType()) {
                    case MUSIC -> createMusicLikeResponse(dropping);
                    case VOTE -> createVoteLikeResponse(dropping);
                    case PLAYLIST -> createPlaylistLikeResponse(dropping);
                });
    }

    private Optional<LikeDroppingResponse> createMusicLikeResponse(Dropping dropping) {
        String songId = dropping.getSongId();
        return songPersistencePort.findById(songId)
                .map(song -> new LikeDroppingResponse(
                        dropping.getDroppingId(),
                        dropping.getDroppingType(),
                        song.getTitle(),
                        song.getAlbumImagePath(),
                        dropping.getAddress()
                ));
    }

    private Optional<LikeDroppingResponse> createVoteLikeResponse(Dropping dropping) {
        VoteDroppingPayload payload = dropping.getVotePayload();
        return Optional.of(new LikeDroppingResponse(
                dropping.getDroppingId(),
                dropping.getDroppingType(),
                payload.getTopic(),
                null,
                dropping.getAddress()
        ));
    }

    private Optional<LikeDroppingResponse> createPlaylistLikeResponse(Dropping dropping) {
        PlaylistDroppingPayload payload = (PlaylistDroppingPayload) dropping.getPayload();
        return Optional.of(new LikeDroppingResponse(
                dropping.getDroppingId(),
                dropping.getDroppingType(),
                payload.getPlaylistName(),
                null,
                dropping.getAddress()
        ));
    }
}
