package org.example.remedy.application.comment;

import lombok.RequiredArgsConstructor;
import org.example.remedy.application.comment.dto.response.CommentResponse;
import org.example.remedy.application.comment.exception.CommentAccessDeniedException;
import org.example.remedy.application.comment.exception.CommentNotFoundException;
import org.example.remedy.application.comment.port.in.CommentService;
import org.example.remedy.application.comment.port.out.CommentPersistencePort;
import org.example.remedy.application.dropping.exception.DroppingNotFoundException;
import org.example.remedy.application.dropping.port.out.DroppingPersistencePort;
import org.example.remedy.application.notification.event.CommentCreatedEvent;
import org.example.remedy.domain.comment.Comment;
import org.example.remedy.domain.dropping.Dropping;
import org.example.remedy.domain.user.User;
import org.example.remedy.presentation.comment.dto.request.CommentUpdateRequest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentPersistencePort commentPersistencePort;
    private final DroppingPersistencePort droppingPersistencePort;
    private final ApplicationEventPublisher eventPublisher;

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
        CommentCreatedEvent event = CommentCreatedEvent.builder()
                .commenterUserId(commenter.getUserId())
                .commenterUsername(commenter.getUsername())
                .droppingOwnerUserId(dropping.getUserId())
                .droppingId(droppingId)
                .commentContent(content)
                .build();
        
        eventPublisher.publishEvent(event);
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
