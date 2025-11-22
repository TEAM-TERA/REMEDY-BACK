package org.example.remedy.domain.comment.application.service;

import lombok.RequiredArgsConstructor;
import org.example.remedy.domain.comment.application.dto.response.CommentResponse;
import org.example.remedy.domain.comment.application.exception.CommentAccessDeniedException;
import org.example.remedy.domain.comment.application.exception.CommentNotFoundException;
import org.example.remedy.domain.comment.application.mapper.CommentMapper;
import org.example.remedy.domain.comment.repository.CommentRepository;
import org.example.remedy.domain.dropping.application.exception.DroppingNotFoundException;
import org.example.remedy.domain.dropping.repository.DroppingRepository;
import org.example.remedy.domain.comment.application.event.CommentCreatedEvent;
import org.example.remedy.domain.comment.domain.Comment;
import org.example.remedy.domain.dropping.domain.Dropping;
import org.example.remedy.domain.user.domain.User;
import org.example.remedy.domain.comment.application.dto.request.CommentUpdateRequest;
import org.example.remedy.global.event.GlobalEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final DroppingRepository droppingRepository;
    private final GlobalEventPublisher eventPublisher;

    @Transactional
    public void createComment(String content, User user, String droppingId) {

        Dropping dropping = droppingRepository.findById(droppingId)
                .orElseThrow(() -> DroppingNotFoundException.EXCEPTION);

        Comment comment = CommentMapper.toEntity(content, user, droppingId);
        commentRepository.save(comment);

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
    public List<CommentResponse> getCommentsByDropping(String droppingId) {

        List<Comment> comments = commentRepository.findAllByDroppingIdDesc(droppingId);

        if (comments.isEmpty() && !droppingRepository.existsById(droppingId)) {
            throw DroppingNotFoundException.EXCEPTION;
        }

        return comments.stream()
                .map(CommentMapper::toResponse)
                .toList();
    }

    @Transactional
    public void updateComment(Long userId, Long commentId ,CommentUpdateRequest request) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(()-> CommentNotFoundException.EXCEPTION);

        validateCommentOwnership(userId, comment);

        comment.updateContent(request.content());

        commentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(Long userId, Long commentId) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(()->CommentNotFoundException.EXCEPTION);

        validateCommentOwnership(userId, comment);

        commentRepository.delete(comment);
    }

    private void validateCommentOwnership(Long userId, Comment comment) {
        if (!comment.getUser().getUserId().equals(userId)) {
            throw CommentAccessDeniedException.EXCEPTION;
        }
    }

    @Transactional(readOnly = true)
    public long countByDroppingId(String droppingId) {

        if (!droppingRepository.existsById(droppingId)) {
            throw DroppingNotFoundException.EXCEPTION;
        }

        return commentRepository.countByDroppingId(droppingId);
    }
}
