package org.example.remedy.application.running.exception;

import org.example.remedy.global.error.ErrorCode;
import org.example.remedy.global.error.exception.NotFoundException;

public class RunningNotFoundException extends NotFoundException {

    public static RunningNotFoundException EXCEPTION = new RunningNotFoundException();

    public RunningNotFoundException() {
        super(ErrorCode.RUNNING_NOT_FOUND);
    }
}
