package org.example.remedy.application.notification.event;

import lombok.Builder;

@Builder
public record CommentCreatedEvent(
        Long commenterUserId,       // 댓글 작성자
        String commenterUsername,   // 댓글 작성자 이름
        Long droppingOwnerUserId,   // 드랍 작성자
        String droppingId,          // 드랍 ID
        String commentContent       // 댓글 내용
) {
    public static CommentCreatedEvent of(Long commenterUserId, String commenterUsername,
                                         Long droppingOwnerUserId, String droppingId, 
                                         String commentContent) {
        return CommentCreatedEvent.builder()
                .commenterUserId(commenterUserId)
                .commenterUsername(commenterUsername)
                .droppingOwnerUserId(droppingOwnerUserId)
                .droppingId(droppingId)
                .commentContent(commentContent)
                .build();
    }
}
