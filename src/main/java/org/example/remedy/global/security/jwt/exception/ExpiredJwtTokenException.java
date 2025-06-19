package org.example.remedy.global.security.jwt.exception;

import org.example.remedy.global.config.error.exception.BusinessBaseException;
import org.example.remedy.global.config.error.ErrorCode;

public class ExpiredJwtTokenException extends BusinessBaseException {
  public static final BusinessBaseException EXCEPTION =
          new ExpiredJwtTokenException();

  private ExpiredJwtTokenException() { super(ErrorCode.EXPIRED_JWT); }
}