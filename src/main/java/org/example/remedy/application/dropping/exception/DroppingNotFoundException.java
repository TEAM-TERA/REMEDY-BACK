package org.example.remedy.application.dropping.exception;

import org.example.remedy.global.error.ErrorCode;
import org.example.remedy.global.error.exception.NotFoundException;

public class DroppingNotFoundException extends NotFoundException {
    public DroppingNotFoundException() {
        super(ErrorCode.DROPPING_NOT_FOUND);
    }
}
