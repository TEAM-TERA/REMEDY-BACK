package org.example.remedy.application.comment;

import lombok.RequiredArgsConstructor;
import org.example.remedy.application.comment.dto.response.CommentResponse;
import org.example.remedy.application.comment.exception.CommentAccessDeniedException;
import org.example.remedy.application.comment.exception.CommentNotFoundException;
import org.example.remedy.application.comment.port.in.CommentService;
import org.example.remedy.application.comment.port.out.CommentPersistencePort;
import org.example.remedy.application.dropping.exception.DroppingNotFoundException;
import org.example.remedy.application.dropping.port.out.DroppingPersistencePort;
import org.example.remedy.application.comment.event.CommentCreatedEvent;
import org.example.remedy.domain.comment.Comment;
import org.example.remedy.domain.dropping.Dropping;
import org.example.remedy.domain.user.User;
import org.example.remedy.presentation.comment.dto.request.CommentUpdateRequest;
import org.example.remedy.global.event.GlobalEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentPersistencePort commentPersistencePort;
    private final DroppingPersistencePort droppingPersistencePort;
    private final GlobalEventPublisher eventPublisher;

    @Transactional
    @Override
    public void createComment(String content, User user, String droppingId) {

        Dropping dropping = droppingPersistencePort.findById(droppingId)
                .orElseThrow(() -> DroppingNotFoundException.EXCEPTION);

        Comment comment = new Comment(content, user, droppingId);
        commentPersistencePort.save(comment);

        publishCommentCreatedEvent(user, dropping, droppingId, content);
    }

    private void publishCommentCreatedEvent(User commenter, Dropping dropping,
                                           String droppingId, String content) {
        // 자기 자신의 Dropping에 댓글을 작성한 경우 알림을 보내지 않음
        if (commenter.getUserId().equals(dropping.getUserId())) {
            return;
        }

        CommentCreatedEvent event = CommentCreatedEvent.builder()
                .commenterUserId(commenter.getUserId())
                .commenterUsername(commenter.getUsername())
                .droppingOwnerUserId(dropping.getUserId())
                .droppingId(droppingId)
                .commentContent(content)
                .build();

        // SSE를 통해 Dropping 소유자에게 댓글 알림 발송
        eventPublisher.publish(dropping.getUserId(), "comment-created", event);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CommentResponse> getCommentsByDropping(String droppingId) {

        List<Comment> comments = commentPersistencePort.findAllByDroppingIdDesc(droppingId);

        if (comments.isEmpty() && !droppingPersistencePort.existsById(droppingId)) {
            throw DroppingNotFoundException.EXCEPTION;
        }

        return comments.stream()
                .map(CommentResponse::of)
                .toList();
    }

    @Transactional
    @Override
    public void updateComment(Long userId, Long commentId ,CommentUpdateRequest request) {

        Comment comment = commentPersistencePort.findById(commentId)
                .orElseThrow(()-> CommentNotFoundException.EXCEPTION);

        validateCommentOwnership(userId, comment);

        comment.updateContent(request.content());

        commentPersistencePort.save(comment);
    }

    @Transactional
    @Override
    public void deleteComment(Long userId, Long commentId) {

        Comment comment = commentPersistencePort.findById(commentId)
                .orElseThrow(()->CommentNotFoundException.EXCEPTION);

        validateCommentOwnership(userId, comment);

        commentPersistencePort.delete(comment);
    }

    private void validateCommentOwnership(Long userId, Comment comment) {
        if (!comment.getUser().getUserId().equals(userId)) {
            throw CommentAccessDeniedException.EXCEPTION;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public long countByDroppingId(String droppingId) {

        if (!droppingPersistencePort.existsById(droppingId)) {
            throw DroppingNotFoundException.EXCEPTION;
        }

        return commentPersistencePort.countByDroppingId(droppingId);
    }
}
