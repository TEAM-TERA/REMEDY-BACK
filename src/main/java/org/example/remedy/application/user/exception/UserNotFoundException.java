package org.example.remedy.application.user.exception;

import org.example.remedy.global.error.ErrorCode;
import org.example.remedy.global.error.exception.NotFoundException;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException() {
      super(ErrorCode.USER_NOT_FOUND);
    }
}
