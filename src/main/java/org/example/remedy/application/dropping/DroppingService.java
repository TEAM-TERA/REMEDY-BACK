package org.example.remedy.application.dropping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.remedy.domain.dropping.Dropping;
import org.example.remedy.interfaces.dropping.dto.request.DroppingCreateRequest;
import org.example.remedy.interfaces.dropping.dto.response.DroppingFindResponse;
import org.example.remedy.interfaces.dropping.dto.response.DroppingSearchListResponse;
import org.example.remedy.interfaces.dropping.dto.response.DroppingSearchResponse;
import org.example.remedy.application.dropping.exception.DroppingNotFoundException;
import org.example.remedy.domain.dropping.DroppingRepository;
import org.example.remedy.domain.dropping.DroppingRepositoryCustom;
import org.example.remedy.global.security.auth.AuthDetails;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DroppingService {
    private final DroppingRepository droppingRepository;
    private final DroppingRepositoryCustom droppingRepositoryCustom;

    @Transactional
    public void createDropping(AuthDetails authDetails, DroppingCreateRequest request) {
        Dropping dropping = Dropping.getInstance(authDetails.getUserId(), request);
        System.out.println(authDetails.getUserId());
        droppingRepositoryCustom.createDropping(dropping);
    }

    public DroppingSearchListResponse searchDroppings(double longitude, double latitude) {
        List<Dropping> allDroppings = droppingRepositoryCustom
                .findActiveDroppingsWithinRadius(longitude, latitude);

        List<DroppingSearchResponse> droppings = allDroppings.stream()
                .map(DroppingSearchResponse::create)
                .toList();

        return DroppingSearchListResponse.newInstance(droppings);
    }

    public DroppingFindResponse getDropping(String droppingId) {
        Dropping dropping = droppingRepository.findById(droppingId)
                .orElseThrow(DroppingNotFoundException::new);
        
        return DroppingFindResponse.newInstance(dropping);
    }

    public List<DroppingSearchResponse> getUserDroppings(Long userId) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        List<Dropping> droppings = droppingRepository.findByUserId(userId, sort);
        
        return droppings.stream()
                .map(DroppingSearchResponse::create)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteDropping(String droppingId, Long userId) {
        Dropping dropping = droppingRepository.findById(droppingId)
                .orElseThrow(DroppingNotFoundException::new);

        if (dropping.getUserId().equals(userId)) droppingRepository.deleteById(droppingId);
    }

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void cleanupExpiredDroppings() {
        List<Dropping> expiredDroppings = droppingRepository.findExpiredAndNotDeletedDroppings(LocalDateTime.now());

        if (expiredDroppings.isEmpty()) return;

        expiredDroppings.forEach(Dropping::markAsDeleted);
        droppingRepository.saveAll(expiredDroppings);

        log.info("만료된 Dropping {}개 자동 soft delete 완료", expiredDroppings.size());
    }
}
