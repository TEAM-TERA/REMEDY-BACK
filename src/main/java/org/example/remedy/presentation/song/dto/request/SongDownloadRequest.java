package org.example.remedy.presentation.song.dto.request;

public record SongDownloadRequest(
        String songTitle,
        String artist
) {
}
