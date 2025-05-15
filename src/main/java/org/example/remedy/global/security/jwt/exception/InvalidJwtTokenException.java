package org.example.remedy.global.security.jwt.exception;

import org.example.remedy.global.config.error.exception.BusinessException;
import org.example.remedy.global.config.error.exception.ErrorCode;

public class InvalidJwtTokenException extends BusinessException {
  public static final BusinessException EXCEPTION =
          new InvalidJwtTokenException();

  private InvalidJwtTokenException() { super(ErrorCode.INVALID_JWT); }
}
