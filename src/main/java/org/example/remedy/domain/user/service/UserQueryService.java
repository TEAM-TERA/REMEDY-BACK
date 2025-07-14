package org.example.remedy.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.example.remedy.domain.like.dto.response.LikeResponse;
import org.example.remedy.domain.like.service.LikeService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserQueryService {

    private final LikeService likeService;

    public List<LikeResponse> getMyLikes(Long userId) {
        return likeService.getMyLikes(userId);
    }
}
