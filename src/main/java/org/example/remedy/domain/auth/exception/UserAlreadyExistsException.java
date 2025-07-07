package org.example.remedy.domain.auth.exception;

import org.example.remedy.global.config.error.ErrorCode;
import org.example.remedy.global.config.error.exception.AlreadyExistsException;

public class UserAlreadyExistsException extends AlreadyExistsException {
    public UserAlreadyExistsException() {
        super(ErrorCode.USER_ALREADY_EXISTS);
    }
}
