package org.example.remedy.domain.user.exception;

import org.example.remedy.global.config.error.ErrorCode;
import org.example.remedy.global.config.error.exception.NotFoundException;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException() {
      super(ErrorCode.USER_NOT_FOUND);
    }
}
