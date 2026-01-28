package org.example.remedy.domain.like.repository;

import org.example.remedy.domain.like.domain.Like;
import org.example.remedy.domain.user.domain.User;

import java.util.List;
import java.util.Optional;

public interface LikeRepository {

    Optional<Like> findByUserAndDroppingId(User user, Long droppingId);
    long countByUser(User user);
    long countByDroppingId(Long droppingId);
    void save(Like like);
    void delete(Like like);
    List<Like> findByUser(User user);
}
