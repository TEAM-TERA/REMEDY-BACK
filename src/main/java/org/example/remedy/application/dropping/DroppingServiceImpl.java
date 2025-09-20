package org.example.remedy.application.dropping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.remedy.application.dropping.port.in.DroppingService;
import org.example.remedy.application.dropping.port.out.DroppingPersistencePort;
import org.example.remedy.domain.dropping.Dropping;
import org.example.remedy.presentation.dropping.dto.request.DroppingCreateRequest;
import org.example.remedy.application.dropping.dto.response.DroppingFindResponse;
import org.example.remedy.application.dropping.dto.response.DroppingSearchListResponse;
import org.example.remedy.application.dropping.dto.response.DroppingSearchResponse;
import org.example.remedy.application.dropping.exception.DroppingNotFoundException;
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
public class DroppingServiceImpl implements DroppingService {

    private final DroppingPersistencePort droppingPersistencePort;

    @Override
    @Transactional
    public void createDropping(AuthDetails authDetails, DroppingCreateRequest request) {
        Dropping dropping = Dropping.getInstance(authDetails.getUserId(), request);
        System.out.println(authDetails.getUserId());
        droppingPersistencePort.createDropping(dropping);
    }

    @Override
    public DroppingSearchListResponse searchDroppings(double longitude, double latitude) {
        List<Dropping> allDroppings = droppingPersistencePort
                .findActiveDroppingsWithinRadius(longitude, latitude);

        List<DroppingSearchResponse> droppings = allDroppings.stream()
                .map(DroppingSearchResponse::create)
                .toList();

        return DroppingSearchListResponse.newInstance(droppings);
    }

    @Override
    public DroppingFindResponse getDropping(String droppingId) {
        Dropping dropping = droppingPersistencePort.findById(droppingId)
                .orElseThrow(() -> DroppingNotFoundException.EXCEPTION);
        
        return DroppingFindResponse.newInstance(dropping);
    }

    @Override
    public List<DroppingSearchResponse> getUserDroppings(Long userId) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        List<Dropping> droppings = droppingPersistencePort.findByUserId(userId, sort);
        
        return droppings.stream()
                .map(DroppingSearchResponse::create)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteDropping(String droppingId, Long userId) {
        Dropping dropping = droppingPersistencePort.findById(droppingId)
                .orElseThrow(() -> DroppingNotFoundException.EXCEPTION);

        if (dropping.getUserId().equals(userId)) droppingPersistencePort.deleteById(droppingId);
    }

    @Override
    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void cleanupExpiredDroppings() {
        List<Dropping> expiredDroppings = droppingPersistencePort.findExpiredAndNotDeletedDroppings(LocalDateTime.now());

        if (expiredDroppings.isEmpty()) return;

        expiredDroppings.forEach(Dropping::markAsDeleted);
        droppingPersistencePort.saveAll(expiredDroppings);

        log.info("만료된 Dropping {}개 자동 soft delete 완료", expiredDroppings.size());
    }
}
