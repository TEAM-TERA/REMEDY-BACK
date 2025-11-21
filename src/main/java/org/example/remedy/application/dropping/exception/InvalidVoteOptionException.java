package org.example.remedy.application.dropping.exception;

import org.example.remedy.global.error.ErrorCode;
import org.example.remedy.global.error.exception.BusinessBaseException;

public class InvalidVoteOptionException extends BusinessBaseException {

    public static final InvalidVoteOptionException EXCEPTION = new InvalidVoteOptionException();

    private InvalidVoteOptionException() {
        super(ErrorCode.INVALID_VOTE_OPTION);
    }
}