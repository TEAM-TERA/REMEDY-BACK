package org.example.remedy.domain.user.application.exception;

import org.example.remedy.global.error.ErrorCode;
import org.example.remedy.global.error.exception.NotFoundException;

public class UserNotFoundException extends NotFoundException {

    public static UserNotFoundException EXCEPTION = new UserNotFoundException();

    private UserNotFoundException() {
      super(ErrorCode.USER_NOT_FOUND);
    }
}
