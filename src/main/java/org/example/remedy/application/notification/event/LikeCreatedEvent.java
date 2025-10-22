package org.example.remedy.application.notification.event;

import lombok.Builder;

@Builder
public record LikeCreatedEvent(
        Long likerUserId,          // 좋아요를 누른 사람
        String likerUsername,      // 좋아요를 누른 사람 이름
        Long droppingOwnerUserId,  // 드랍 작성자
        String droppingId          // 드랍 ID
) {
    public static LikeCreatedEvent of(Long likerUserId, String likerUsername, 
                                      Long droppingOwnerUserId, String droppingId) {
        return LikeCreatedEvent.builder()
                .likerUserId(likerUserId)
                .likerUsername(likerUsername)
                .droppingOwnerUserId(droppingOwnerUserId)
                .droppingId(droppingId)
                .build();
    }
}
