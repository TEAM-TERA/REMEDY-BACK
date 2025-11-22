package org.example.remedy.domain.comment.application.exception;

import org.example.remedy.global.error.ErrorCode;
import org.example.remedy.global.error.exception.BusinessBaseException;

public class CommentAccessDeniedException extends BusinessBaseException {

    public static CommentAccessDeniedException EXCEPTION = new CommentAccessDeniedException();

    private CommentAccessDeniedException() {
        super(ErrorCode.COMMENT_ACCESS_DENIED);
    }
}
