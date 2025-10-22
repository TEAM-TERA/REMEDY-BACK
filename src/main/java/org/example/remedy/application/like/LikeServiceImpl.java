package org.example.remedy.application.like;

import lombok.RequiredArgsConstructor;

import org.example.remedy.application.dropping.exception.DroppingNotFoundException;
import org.example.remedy.application.dropping.port.out.DroppingPersistencePort;
import org.example.remedy.application.like.port.in.LikeService;
import org.example.remedy.application.like.port.out.LikePersistencePort;
import org.example.remedy.application.notification.event.LikeCreatedEvent;
import org.example.remedy.domain.dropping.Dropping;
import org.example.remedy.domain.like.Like;
import org.example.remedy.domain.user.User;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {
    private final LikePersistencePort likePersistencePort;
    private final DroppingPersistencePort droppingPersistencePort;
    private final ApplicationEventPublisher eventPublisher;

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
        LikeCreatedEvent event = LikeCreatedEvent.builder()
                .likerUserId(liker.getUserId())
                .likerUsername(liker.getUsername())
                .droppingOwnerUserId(dropping.getUserId())
                .droppingId(droppingId)
                .build();
        
        eventPublisher.publishEvent(event);
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
    public List<String> getLikedDroppingsByUser(User user) {
        return likePersistencePort.findByUser(user).stream()
                .map(Like::getDroppingId)
                .toList();
    }
}
