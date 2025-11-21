package org.example.remedy.application.dropping.dto.response;

import java.util.List;

public record DroppingSearchListResponse(
        List<DroppingResponse> droppings
) {
    public static DroppingSearchListResponse of(List<DroppingResponse> droppings) {
        return new DroppingSearchListResponse(droppings);
    }
}
