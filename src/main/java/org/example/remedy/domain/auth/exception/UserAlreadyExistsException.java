package org.example.remedy.domain.auth.exception;

import org.example.remedy.global.error.ErrorCode;
import org.example.remedy.global.error.exception.AlreadyExistsException;

public class UserAlreadyExistsException extends AlreadyExistsException {
    public UserAlreadyExistsException() {
        super(ErrorCode.USER_ALREADY_EXISTS);
    }
}
