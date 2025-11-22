package org.example.remedy.domain.like.repository;

import lombok.RequiredArgsConstructor;
import org.example.remedy.domain.like.domain.Like;
import org.example.remedy.domain.user.domain.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class LikeRepositoryImpl implements LikeRepository {

    private final LikePersistenceRepository likePersistenceRepository;

    @Override
    public Optional<Like> findByUserAndDroppingId(User user, String droppingId) {
        return likePersistenceRepository.findByUserAndDroppingId(user, droppingId);
    }

    @Override
    public long countByUser(User user) {
        return likePersistenceRepository.countByUser(user);
    }

    @Override
    public long countByDroppingId(String droppingId) {
        return likePersistenceRepository.countByDroppingId(droppingId);
    }

    @Override
    public void save(Like like) {
        likePersistenceRepository.save(like);
    }

    @Override
    public void delete(Like like) {
        likePersistenceRepository.delete(like);
    }

    @Override
    public List<Like> findByUser(User user) {
        return likePersistenceRepository.findByUser(user);
    }
}
