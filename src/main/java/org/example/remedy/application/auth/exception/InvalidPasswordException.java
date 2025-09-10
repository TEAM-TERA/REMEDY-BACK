package org.example.remedy.application.auth.exception;

import org.example.remedy.global.error.ErrorCode;
import org.example.remedy.global.error.exception.AlreadyExistsException;

public class InvalidPasswordException extends AlreadyExistsException {

    public static InvalidPasswordException EXCEPTION = new InvalidPasswordException();

    private InvalidPasswordException() {
        super(ErrorCode.INVALID_PASSWORD);
    }
}