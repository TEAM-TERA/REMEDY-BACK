package org.example.remedy.application.like;

import lombok.RequiredArgsConstructor;

import org.example.remedy.application.dropping.exception.DroppingNotFoundException;
import org.example.remedy.application.like.port.in.LikeService;
import org.example.remedy.application.like.port.out.LikePersistencePort;
import org.example.remedy.domain.dropping.Dropping;
import org.example.remedy.domain.dropping.DroppingRepository;
import org.example.remedy.domain.like.Like;
import org.example.remedy.infrastructure.persistence.like.LikeRepository;
import org.example.remedy.application.user.exception.UserNotFoundException;
import org.example.remedy.domain.user.User;
import org.example.remedy.infrastructure.persistence.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {
    private final LikePersistencePort likePersistencePort;
    private final UserRepository userRepository;
    private final DroppingRepository droppingRepository;

    @Override
    @Transactional
    public boolean toggleLike(Long userId, String droppingId) {
        User user = userRepository.findById(userId).
                orElseThrow(UserNotFoundException::new);

        if (!droppingRepository.existsById(droppingId)) {
            throw new DroppingNotFoundException();
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
    public long getLikeCountByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        return likePersistencePort.countByUser(user);
    }

    @Override
    @Transactional(readOnly = true)
    public long getLikeCountByDropping(String droppingId) {
        Dropping dropping = droppingRepository.findById(droppingId)
                .orElseThrow(DroppingNotFoundException::new);
        return likePersistencePort.countByDroppingId(droppingId);
    }
}
