package org.example.remedy.application.title.exception;

import org.example.remedy.global.error.ErrorCode;
import org.example.remedy.global.error.exception.AlreadyExistsException;

public class TitleAlreadyExistsException extends AlreadyExistsException {
    public static final TitleAlreadyExistsException INSTANCE = new TitleAlreadyExistsException();

    private TitleAlreadyExistsException() {
        super(ErrorCode.TITLE_ALREADY_EXISTS);
    }
}