package org.example.remedy.domain.like.service;

import lombok.RequiredArgsConstructor;
import org.example.remedy.domain.dropping.domain.Dropping;
import org.example.remedy.domain.dropping.exception.DroppingNotFoundException;
import org.example.remedy.domain.dropping.repository.DroppingRepository;
import org.example.remedy.domain.like.domain.Like;
import org.example.remedy.domain.like.repository.LikeRepository;
import org.example.remedy.domain.user.domain.User;
import org.example.remedy.domain.user.exception.UserNotFoundException;
import org.example.remedy.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final DroppingRepository droppingRepository;

    public boolean toggleLike(Long userId, String droppingId) {
        User user = userRepository.findById(userId).
                orElseThrow(UserNotFoundException::new);

        if (!droppingRepository.existsById(droppingId)) {
            throw new DroppingNotFoundException();
        }

        Optional<Like> existingLike = likeRepository.findByUserAndDroppingId(user, droppingId);

        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
            return false;
        } else {
            Like like = new Like(user, droppingId);
            likeRepository.save(like);
            return true;
        }

    }
    public long getLikeCountByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        return likeRepository.countByUser(user);
    }

    public long getLikeCountByDropping(String droppingId) {
        Dropping dropping = droppingRepository.findById(droppingId)
                .orElseThrow(DroppingNotFoundException::new);
        return likeRepository.countByDroppingId(droppingId);
    }
}
