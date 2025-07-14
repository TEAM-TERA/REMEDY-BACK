package org.example.remedy.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.example.remedy.domain.dropping.domain.Dropping;
import org.example.remedy.domain.dropping.repository.DroppingRepository;
import org.example.remedy.domain.like.dto.response.LikeResponse;
import org.example.remedy.domain.like.service.LikeService;
import org.example.remedy.domain.user.dto.response.MyDroppingResponse;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserQueryService {

    private final LikeService likeService;
    private final DroppingRepository droppingRepository;

    public List<LikeResponse> getMyLikes(Long userId) {
        return likeService.getMyLikes(userId);
    }

    public List<MyDroppingResponse> getUserDroppings(Long userId) {
        List<Dropping> droppings = droppingRepository.findByUserId(
                userId,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        return droppings.stream()
                .map(MyDroppingResponse::from)
                .toList();
    }
}
