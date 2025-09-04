package org.example.remedy.presentation.dropping.dto.response;

import java.util.List;

public record DroppingSearchListResponse(
        List<DroppingSearchResponse> droppings
) {
    public static DroppingSearchListResponse newInstance(List<DroppingSearchResponse> droppings) {
        return new DroppingSearchListResponse(droppings);
    }
}
