package org.example.remedy.infrastructure.persistence.like;

import org.example.remedy.domain.like.Like;
import org.example.remedy.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUserAndDroppingId(User user, String droppingId);

    long countByUser(User user);

    long countByDroppingId(String droppingId);

    List<Like> findByUser(User user);

}

