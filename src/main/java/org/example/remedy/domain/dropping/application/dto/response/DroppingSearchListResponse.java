package org.example.remedy.domain.dropping.application.dto.response;

import java.util.List;

public record DroppingSearchListResponse(
        List<DroppingResponse> droppings
) {
}
