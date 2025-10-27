package org.example.remedy.application.song.dto.response;

public record SongDownloadResponse(
        boolean success,
        String songId,
        String title,
        String artist,
        String hlsPath,
        String albumImagePath,
        String errorMessage
) {
    public static SongDownloadResponse success(String songId, String title, String artist, String hlsPath, String albumImagePath) {
        return new SongDownloadResponse(
                true,
                songId,
                title,
                artist,
                hlsPath,
                albumImagePath,
                ""
        );
    }

    public static SongDownloadResponse failure(String title, String errorMessage) {
        return new SongDownloadResponse(
                false,
                null,   // songId
                title,
                null,   // artist
                null,   // hlsPath
                null,   // albumImagePath
                errorMessage
        );
    }

    public static boolean isSuccess(SongDownloadResponse songDownloadResponse) {
        return songDownloadResponse.success;
    }
}
