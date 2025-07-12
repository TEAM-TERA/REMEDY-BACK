package org.example.remedy.domain.like.repository;

import org.example.remedy.domain.like.domain.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.example.remedy.domain.like.domain.TargetType;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUserIdAndTargetIdAndTargetType(Long userId, String targetId, TargetType targetType);

    List<Like> findByUserId(Long userId);
    Long countByTargetIdAndTargetType(String targetId, TargetType targetType);
}
