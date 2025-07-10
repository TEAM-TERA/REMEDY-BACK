package org.example.remedy.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.example.remedy.domain.dropping.domain.Dropping;
import org.example.remedy.domain.dropping.repository.DroppingRepository;
import org.example.remedy.domain.user.dto.response.MyDroppingResponse;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyDroppingService {

    private final DroppingRepository droppingRepository;

    public List<MyDroppingResponse> getMyDroppings(Long userId) {
        List<Dropping> droppings = droppingRepository.findByUserId(
                userId,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        return droppings.stream()
                .map(MyDroppingResponse::from)
                .collect(Collectors.toList());
    }
}
