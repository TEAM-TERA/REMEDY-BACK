package org.example.remedy.application.running.exception;

import org.example.remedy.global.error.ErrorCode;
import org.example.remedy.global.error.exception.AlreadyExistsException;

public class RunningAlreadyExistsException extends AlreadyExistsException {

    public static RunningAlreadyExistsException EXCEPTION = new RunningAlreadyExistsException();

    public RunningAlreadyExistsException() {
        super(ErrorCode.RUNNING_ALREADY_EXISTS);
    }
}
