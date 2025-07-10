package org.example.remedy.domain.user.dto.response;

import org.example.remedy.domain.dropping.domain.Dropping;



public record MyDroppingResponse(
        String droppingId,
        String content,
        String songId,
        String address,
        Double latitude,
        Double longitude
) {
    public static MyDroppingResponse from(Dropping dropping) {
        return new MyDroppingResponse(
                dropping.getDroppingId(),
                dropping.getContent(),
                dropping.getSongId(),
                dropping.getAddress(),
                dropping.getLatitude(),
                dropping.getLongitude()
        );
    }
}

