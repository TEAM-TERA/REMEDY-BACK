package org.example.remedy.application.song.exception;

import org.example.remedy.global.error.ErrorCode;
import org.example.remedy.global.error.exception.NotFoundException;

public class MetadataNotFoundException extends NotFoundException {

    public static MetadataNotFoundException EXCEPTION = new MetadataNotFoundException();

    private MetadataNotFoundException() {
        super(ErrorCode.METADATA_NOT_FOUND);
    }
}
