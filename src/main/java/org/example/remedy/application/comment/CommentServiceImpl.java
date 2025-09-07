package org.example.remedy.application.comment;

import lombok.RequiredArgsConstructor;
import org.example.remedy.application.comment.port.in.CommentService;
import org.example.remedy.application.comment.port.out.CommentPersistencePort;
import org.example.remedy.application.dropping.exception.DroppingNotFoundException;
import org.example.remedy.application.user.exception.UserNotFoundException;
import org.example.remedy.application.user.port.out.UserPersistencePort;
import org.example.remedy.domain.comment.Comment;
import org.example.remedy.domain.dropping.DroppingRepository;
import org.example.remedy.domain.user.User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentPersistencePort commentPersistencePort;
    private final DroppingRepository droppingRepository;
    private final UserPersistencePort userPersistencePort;

    @Override
    public void createComment(String content, Long userId, String droppingId) {

        User user = userPersistencePort.findById(userId).
                orElseThrow(UserNotFoundException::new);

        if (!droppingRepository.existsById(droppingId)) {
            throw new DroppingNotFoundException();
        }

        Comment comment = new Comment(content, user, droppingId);
        commentPersistencePort.save(comment);
    }
}
