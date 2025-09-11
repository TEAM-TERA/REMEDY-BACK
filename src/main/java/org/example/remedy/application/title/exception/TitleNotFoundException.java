package org.example.remedy.application.title.exception;

import org.example.remedy.global.error.ErrorCode;
import org.example.remedy.global.error.exception.NotFoundException;

public class TitleNotFoundException extends NotFoundException {
    public static final TitleNotFoundException INSTANCE = new TitleNotFoundException();

    private TitleNotFoundException() {
        super(ErrorCode.TITLE_NOT_FOUND);
    }
}