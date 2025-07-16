package org.example.remedy.domain.like.service;

import lombok.RequiredArgsConstructor;
import org.example.remedy.domain.like.domain.Like;
import org.example.remedy.domain.like.dto.response.LikeResponse;
import org.example.remedy.domain.like.repository.LikeRepository;
import org.springframework.stereotype.Service;
import org.example.remedy.domain.like.domain.TargetType;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;

    public void like(Long userId, String targetId, TargetType targetType) {
        Optional<Like> existingLike = likeRepository.findByUserIdAndTargetIdAndTargetType(userId, targetId, targetType);

        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
        } else {
            Like like = Like.getInstance(userId, targetId, targetType);
            likeRepository.save(like);
        }
    }

    public List<LikeResponse> getUserLikes(Long userId) {
        return likeRepository.findByUserId(userId).stream()
                .map(LikeResponse::of)
                .collect(Collectors.toList());
    }

    public long countLikes(String targetId, TargetType targetType) {
        return likeRepository.countByTargetIdAndTargetType(targetId, targetType);
    }
}