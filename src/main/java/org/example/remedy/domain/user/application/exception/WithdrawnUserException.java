package org.example.remedy.domain.user.application.exception;

import org.example.remedy.global.error.ErrorCode;
import org.example.remedy.global.error.exception.BusinessBaseException;

public class WithdrawnUserException extends BusinessBaseException {

    public static WithdrawnUserException EXCEPTION = new WithdrawnUserException();

    private WithdrawnUserException() {
        super(ErrorCode.USER_WITHDRAWN);
    }
}