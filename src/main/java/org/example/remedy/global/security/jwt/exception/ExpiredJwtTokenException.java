package org.example.remedy.global.security.jwt.exception;

import org.example.remedy.global.config.error.exception.BusinessException;
import org.example.remedy.global.config.error.exception.ErrorCode;

public class ExpiredJwtTokenException extends BusinessException {
  public static final BusinessException EXCEPTION =
          new ExpiredJwtTokenException();

  private ExpiredJwtTokenException() { super(ErrorCode.EXPIRED_JWT); }
}