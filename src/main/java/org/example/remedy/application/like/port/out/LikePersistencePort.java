package org.example.remedy.application.like.port.out;

import org.example.remedy.domain.like.Like;
import org.example.remedy.domain.user.User;

import java.util.Optional;

public interface LikePersistencePort {

    Optional<Like> findByUserAndDroppingId(User user, String droppingId);
    long countByUser(User user);
    long countByDroppingId(String droppingId);
    void save(Like like);
    void delete(Like like);
}
