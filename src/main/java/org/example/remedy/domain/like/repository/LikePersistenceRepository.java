package org.example.remedy.domain.like.repository;

import org.example.remedy.domain.like.domain.Like;
import org.example.remedy.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikePersistenceRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUserAndDroppingId(User user, Long droppingId);

    long countByUser(User user);

    long countByDroppingId(Long droppingId);

    List<Like> findByUser(User user);

}

