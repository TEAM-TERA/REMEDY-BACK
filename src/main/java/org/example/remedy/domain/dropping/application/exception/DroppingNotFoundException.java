package org.example.remedy.domain.dropping.application.exception;

import org.example.remedy.global.error.ErrorCode;
import org.example.remedy.global.error.exception.NotFoundException;

public class DroppingNotFoundException extends NotFoundException {

    public static DroppingNotFoundException EXCEPTION = new DroppingNotFoundException();

    private DroppingNotFoundException() {
        super(ErrorCode.DROPPING_NOT_FOUND);
    }
}
