package org.example.remedy.domain.dropping.application.exception;

import org.example.remedy.global.error.ErrorCode;
import org.example.remedy.global.error.exception.BusinessBaseException;

public class EmptyVoteOptionsException extends BusinessBaseException {

    public static EmptyVoteOptionsException EXCEPTION = new EmptyVoteOptionsException();

    private EmptyVoteOptionsException() {
        super(ErrorCode.EMPTY_VOTE_OPTIONS);
    }
}
