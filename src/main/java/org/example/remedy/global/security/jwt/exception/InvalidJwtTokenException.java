package org.example.remedy.global.security.jwt.exception;

import org.example.remedy.global.config.error.exception.BusinessBaseException;
import org.example.remedy.global.config.error.ErrorCode;

public class InvalidJwtTokenException extends BusinessBaseException {
  public static final BusinessBaseException EXCEPTION =
          new InvalidJwtTokenException();

  private InvalidJwtTokenException() { super(ErrorCode.INVALID_JWT); }
}
