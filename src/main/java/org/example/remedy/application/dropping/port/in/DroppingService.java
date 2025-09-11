package org.example.remedy.application.dropping.port.in;

import org.example.remedy.application.dropping.dto.response.DroppingFindResponse;
import org.example.remedy.application.dropping.dto.response.DroppingSearchListResponse;
import org.example.remedy.application.dropping.dto.response.DroppingSearchResponse;
import org.example.remedy.global.security.auth.AuthDetails;
import org.example.remedy.presentation.dropping.dto.request.DroppingCreateRequest;

import java.util.List;

public interface DroppingService {

    void createDropping(AuthDetails authDetails, DroppingCreateRequest request);

    DroppingFindResponse getDropping(String droppingId);

    DroppingSearchListResponse searchDroppings(double longitude, double latitude);

    List<DroppingSearchResponse> getUserDroppings(Long userId);

    void deleteDropping(String droppingId, Long userId);

    void cleanupExpiredDroppings();
}
