package org.example.remedy.infrastructure.persistence.like;

import lombok.RequiredArgsConstructor;
import org.example.remedy.application.like.port.out.LikePersistencePort;
import org.example.remedy.domain.like.Like;
import org.example.remedy.domain.user.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaLikeAdapter implements LikePersistencePort {

    private final LikeRepository likeRepository;

    @Override
    public Optional<Like> findByUserAndDroppingId(User user, String droppingId) {
        return likeRepository.findByUserAndDroppingId(user, droppingId);
    }

    @Override
    public long countByUser(User user) {
        return likeRepository.countByUser(user);
    }

    @Override
    public long countByDroppingId(String droppingId) {
        return likeRepository.countByDroppingId(droppingId);
    }

    @Override
    public void save(Like like) {
        likeRepository.save(like);
    }

    @Override
    public void delete(Like like) {
        likeRepository.delete(like);
    }

    @Override
    public List<Like> findByUser(User user) {
        return likeRepository.findByUser(user);
    }
}
