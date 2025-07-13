package org.example.remedy.domain.song.exception;

import org.example.remedy.global.config.error.ErrorCode;
import org.example.remedy.global.config.error.exception.NotFoundException;

public class MetadataNotFoundException extends NotFoundException {
    public MetadataNotFoundException() {
        super(ErrorCode.METADATA_NOT_FOUND);
    }
}
