package org.example.remedy.application.comment.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 댓글 생성 이벤트 DTO
 * SSE를 통해 Dropping 소유자에게 전송됩니다.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreatedEvent {

    /**
     * 댓글을 작성한 사용자 ID
     */
    private Long commenterUserId;

    /**
     * 댓글을 작성한 사용자 이름
     */
    private String commenterUsername;

    /**
     * Dropping 소유자 사용자 ID (알림 수신자)
     */
    private Long droppingOwnerUserId;

    /**
     * 댓글이 작성된 Dropping ID
     */
    private String droppingId;

    /**
     * 댓글 내용
     */
    private String commentContent;
}
