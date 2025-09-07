package org.example.remedy.application.comment;

import lombok.RequiredArgsConstructor;
import org.example.remedy.application.comment.dto.response.CommentResponse;
import org.example.remedy.application.comment.port.in.CommentService;
import org.example.remedy.application.comment.port.out.CommentPersistencePort;
import org.example.remedy.application.dropping.exception.DroppingNotFoundException;
import org.example.remedy.domain.comment.Comment;
import org.example.remedy.domain.dropping.DroppingRepository;
import org.example.remedy.domain.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentPersistencePort commentPersistencePort;
    private final DroppingRepository droppingRepository;

    @Transactional
    @Override
    public void createComment(String content, User user, String droppingId) {

        if (!droppingRepository.existsById(droppingId)) {
            throw new DroppingNotFoundException();
        }

        Comment comment = new Comment(content, user, droppingId);
        commentPersistencePort.save(comment);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CommentResponse> getCommentsByDropping(String droppingId) {

        List<Comment> comments = commentPersistencePort.findAllByDroppingIdDesc(droppingId);

        if (comments.isEmpty() && !droppingRepository.existsById(droppingId)) {
            throw new DroppingNotFoundException();
        }

        return comments.stream()
                .map(c -> new CommentResponse(c.getId(), c.getContent(), c.getDroppingId()))
                .toList();
    }
}
