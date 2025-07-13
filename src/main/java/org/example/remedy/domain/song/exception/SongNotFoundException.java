package org.example.remedy.domain.song.exception;

import org.example.remedy.global.config.error.ErrorCode;
import org.example.remedy.global.config.error.exception.NotFoundException;

public class SongNotFoundException extends NotFoundException {
    public SongNotFoundException() {
        super(ErrorCode.SONG_NOT_FOUND);
    }
}
