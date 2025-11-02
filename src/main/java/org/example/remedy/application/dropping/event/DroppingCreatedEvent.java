package org.example.remedy.application.dropping.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Dropping 생성 이벤트 DTO
 * SSE를 통해 클라이언트에게 전송됩니다.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DroppingCreatedEvent {

    /**
     * Dropping을 생성한 사용자 ID
     */
    private Long userId;

    /**
     * Dropping에 포함된 곡 ID
     */
    private String songId;
}
