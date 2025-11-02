package org.example.remedy.application.like.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 좋아요 생성 이벤트 DTO
 * SSE를 통해 Dropping 소유자에게 전송됩니다.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeCreatedEvent {

    /**
     * 좋아요를 누른 사용자 ID
     */
    private Long likerUserId;

    /**
     * 좋아요를 누른 사용자 이름
     */
    private String likerUsername;

    /**
     * Dropping 소유자 사용자 ID (알림 수신자)
     */
    private Long droppingOwnerUserId;

    /**
     * 좋아요가 눌린 Dropping ID
     */
    private String droppingId;
}
