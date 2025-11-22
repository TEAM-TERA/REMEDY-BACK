package org.example.remedy.domain.dropping.application.exception;

import org.example.remedy.global.error.ErrorCode;
import org.example.remedy.global.error.exception.AlreadyExistsException;

public class DroppingAlreadyExistsException extends AlreadyExistsException {

  public static DroppingAlreadyExistsException EXCEPTION = new DroppingAlreadyExistsException();

  private DroppingAlreadyExistsException() {
    super(ErrorCode.DROPPING_ALREADY_EXISTS);
  }
}