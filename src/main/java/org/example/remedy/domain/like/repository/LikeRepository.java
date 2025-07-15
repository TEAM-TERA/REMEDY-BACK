package org.example.remedy.domain.like.repository;

import org.example.remedy.domain.like.domain.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.example.remedy.domain.like.domain.TargetType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends MongoRepository<Like, String> {
    Optional<Like> findByUserIdAndTargetIdAndTargetType(Long userId, String targetId, TargetType targetType);

    List<Like> findByUserId(Long userId);

    Long countByTargetIdAndTargetType(String targetId, TargetType targetType);
}
