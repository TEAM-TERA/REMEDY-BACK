package org.example.remedy.domain.song.exception;

import org.example.remedy.global.error.ErrorCode;
import org.example.remedy.global.error.exception.NotFoundException;

public class SongNotFoundException extends NotFoundException {
    public SongNotFoundException() {
        super(ErrorCode.SONG_NOT_FOUND);
    }
}
