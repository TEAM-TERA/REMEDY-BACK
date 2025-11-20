package org.example.remedy.application.dropping.dto.response;

import org.example.remedy.domain.dropping.DroppingType;


public interface DroppingResponse {
    DroppingType type();
    String droppingId();
    Long userId();
    Double latitude();
    Double longitude();
    String address();
}