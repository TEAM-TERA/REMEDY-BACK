package org.example.remedy.domain.user.exception;

import org.example.remedy.global.config.error.exception.BusinessException;
import org.example.remedy.global.config.error.exception.ErrorCode;

public class UserNotFoundException extends BusinessException {
  public static final BusinessException EXCEPTION =
          new UserNotFoundException();

  private UserNotFoundException() {super(ErrorCode.USER_NOT_FOUND);}
}
