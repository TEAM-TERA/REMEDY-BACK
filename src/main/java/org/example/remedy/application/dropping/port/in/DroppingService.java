package org.example.remedy.application.dropping.port.in;

import org.example.remedy.application.dropping.dto.response.DroppingFindResponse;
import org.example.remedy.application.dropping.dto.response.DroppingSearchListResponse;

public interface DroppingService {

    DroppingSearchListResponse searchDroppings(double longitude, double latitude);

    DroppingFindResponse getDropping(String droppingId);

    DroppingSearchListResponse getUserDroppings(Long userId);

    void deleteDropping(String droppingId, Long userId);

    void cleanupExpiredDroppings();
}