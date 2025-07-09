package org.example.remedy.domain.dropping.exception;

import org.example.remedy.global.config.error.ErrorCode;
import org.example.remedy.global.config.error.exception.NotFoundException;

public class DroppingNotFoundException extends NotFoundException {
    public DroppingNotFoundException() {
        super(ErrorCode.DROPPING_NOT_FOUND);
    }
}
