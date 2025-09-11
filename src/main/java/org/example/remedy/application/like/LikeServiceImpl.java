package org.example.remedy.application.like;

import lombok.RequiredArgsConstructor;

import org.example.remedy.application.dropping.exception.DroppingNotFoundException;
import org.example.remedy.application.dropping.port.out.DroppingPersistencePort;
import org.example.remedy.application.like.port.in.LikeService;
import org.example.remedy.application.like.port.out.LikePersistencePort;
import org.example.remedy.application.user.port.out.UserPersistencePort;
import org.example.remedy.infrastructure.persistence.dropping.DroppingRepository;
import org.example.remedy.domain.like.Like;
import org.example.remedy.domain.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {
    private final LikePersistencePort likePersistencePort;
    private final DroppingPersistencePort droppingPersistencePort;

    @Override
    @Transactional
    public boolean toggleLike(User user, String droppingId) {

        if (!droppingPersistencePort.existsById(droppingId)) {
            throw DroppingNotFoundException.EXCEPTION;
        }

        Optional<Like> existingLike = likePersistencePort.findByUserAndDroppingId(user, droppingId);

        if (existingLike.isPresent()) {
            likePersistencePort.delete(existingLike.get());
            return false;
        } else {
            Like like = new Like(user, droppingId);
            likePersistencePort.save(like);
            return true;
        }

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
