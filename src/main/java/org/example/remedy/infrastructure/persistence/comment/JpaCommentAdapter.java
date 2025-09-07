package org.example.remedy.infrastructure.persistence.comment;

import lombok.RequiredArgsConstructor;
import org.example.remedy.application.comment.port.out.CommentPersistencePort;
import org.example.remedy.domain.comment.Comment;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class JpaCommentAdapter implements CommentPersistencePort {

    private final CommentRepository commentRepository;

    @Override
    public void save(Comment comment) {
        commentRepository.save(comment);
    }
}
