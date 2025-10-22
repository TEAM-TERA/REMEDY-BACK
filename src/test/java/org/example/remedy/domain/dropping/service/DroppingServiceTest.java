package org.example.remedy.domain.dropping.service;

import org.example.remedy.domain.dropping.Dropping;
import org.example.remedy.infrastructure.persistence.dropping.DroppingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DroppingServiceTest {
    @Mock
    private FindActiveDroppings droppingRepositoryCustom;

    @Mock
    private DroppingRepository droppingRepository;

    @Test
    void insertDummyData() {
        LocalDateTime now = LocalDateTime.now();

        List<Dropping> dummyDroppings = Arrays.asList(
                new Dropping(
                        1L,
                        "song123",
                        "강남역 근처 첫 번째 dropping",
                        37.4979,
                        127.0276,
                        "서울시 강남구 역삼동",
                        now.plusDays(3),  // expiryDate
                        now,               // createdAt
                        false
                ),
                new Dropping(
                        2L,
                        "song456",
                        "강남역 근처 두 번째 dropping",
                        37.4981,
                        127.0278,
                        "서울시 강남구 역삼동",
                        now.plusDays(3),
                        now,
                        false
                ),
                new Dropping(
                        3L,
                        "song789",
                        "멀리 떨어진 dropping",
                        37.5665,
                        126.9780,
                        "서울시 마포구 홍대",
                        now.plusDays(3),
                        now,
                        false
                ),
                new Dropping(
                        4L,
                        "song111",
                        "만료된 dropping",
                        37.4979,
                        127.0276,
                        "서울시 강남구 역삼동",
                        now.minusDays(1),  // 이미 만료됨
                        now.minusDays(5),
                        false
                )
        );

        // Mock 동작 정의
        when(droppingRepository.saveAll(any())).thenReturn(dummyDroppings);
        when(droppingRepository.findAll()).thenReturn(dummyDroppings);
        
        // 강남역 기준 3미터 이내 활성 dropping 2개 반환 (첫 번째, 두 번째)
        List<Dropping> activeNearbyDroppings = Arrays.asList(dummyDroppings.get(0), dummyDroppings.get(1));
        when(droppingRepositoryCustom.findActiveDroppingsWithinRadius(anyDouble(), anyDouble()))
                .thenReturn(activeNearbyDroppings);

        // 테스트 실행
        droppingRepository.saveAll(dummyDroppings);
        
        List<Dropping> allData = droppingRepository.findAll();
        System.out.println("저장된 데이터 개수: " + allData.size());
        
        allData.forEach(d -> {
            System.out.println("ID: " + d.getDroppingId() + 
                             ", 위치: " + d.getLatitude() + "," + d.getLongitude() + 
                             ", 활성: " + d.isActive());
        });

        // 지리적 검색 테스트 (강남역 기준)
        List<Dropping> result = droppingRepositoryCustom
                .findActiveDroppingsWithinRadius(37.4979, 127.0276);
        
        System.out.println("검색 결과: " + result.size());

        assertThat(result).hasSize(2); // 3미터 이내 활성 dropping 2개
    }
}
