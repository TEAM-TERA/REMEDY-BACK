package org.example.remedy.domain.dropping.application.dto.response;

import org.example.remedy.domain.dropping.domain.DroppingType;


public interface DroppingResponse {
    DroppingType type();
    String droppingId();
    Long userId();
    Double latitude();
    Double longitude();
    String address();
}