package org.example.remedy.application.storage.exception;

import org.example.remedy.global.error.ErrorCode;
import org.example.remedy.global.error.exception.BusinessBaseException;

public class FileUploadFailedException extends BusinessBaseException {
    public static final FileUploadFailedException EXCEPTION = new FileUploadFailedException();

    private FileUploadFailedException() {
        super(ErrorCode.FILE_UPLOAD_FAILED);
    }
}