package org.example.remedy.domain.dropping.exception;

import org.example.remedy.global.config.error.ErrorCode;
import org.example.remedy.global.config.error.exception.AlreadyExistsException;

public class DroppingAlreadyExistsException extends AlreadyExistsException {
  public DroppingAlreadyExistsException() {
    super(ErrorCode.DROPPING_ALREADY_EXISTS);
  }
}