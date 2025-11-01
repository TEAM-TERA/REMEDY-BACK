package org.example.remedy.application.song.dto;

/**
 * 곡 배치 처리 중간 결과를 담는 DTO
 */
public class SongProcessingResult {
    private final String songId;
    private final String title;
    private final String artist;
    private final int duration;
    private final String mp3LocalPath;
    private String hlsLocalDir;
    private String mp3S3Url;
    private String hlsS3Url;
    private final String albumImageS3Url;
    private boolean success;
    private String errorMessage;

    private SongProcessingResult(Builder builder) {
        this.songId = builder.songId;
        this.title = builder.title;
        this.artist = builder.artist;
        this.duration = builder.duration;
        this.mp3LocalPath = builder.mp3LocalPath;
        this.hlsLocalDir = builder.hlsLocalDir;
        this.mp3S3Url = builder.mp3S3Url;
        this.hlsS3Url = builder.hlsS3Url;
        this.albumImageS3Url = builder.albumImageS3Url;
        this.success = builder.success;
        this.errorMessage = builder.errorMessage;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static SongProcessingResult failed(String title, String artist, String errorMessage) {
        return builder()
                .title(title)
                .artist(artist)
                .success(false)
                .errorMessage(errorMessage)
                .build();
    }

    // Getters
    public String getSongId() { return songId; }
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public int getDuration() { return duration; }
    public String getMp3LocalPath() { return mp3LocalPath; }
    public String getHlsLocalDir() { return hlsLocalDir; }
    public String getMp3S3Url() { return mp3S3Url; }
    public String getHlsS3Url() { return hlsS3Url; }
    public String getAlbumImageS3Url() { return albumImageS3Url; }
    public boolean isSuccess() { return success; }
    public String getErrorMessage() { return errorMessage; }

    // Setters (상태 변경용)
    public void setHlsLocalDir(String hlsLocalDir) { this.hlsLocalDir = hlsLocalDir; }
    public void setMp3S3Url(String mp3S3Url) { this.mp3S3Url = mp3S3Url; }
    public void setHlsS3Url(String hlsS3Url) { this.hlsS3Url = hlsS3Url; }
    public void setSuccess(boolean success) { this.success = success; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public static class Builder {
        private String songId;
        private String title;
        private String artist;
        private int duration;
        private String mp3LocalPath;
        private String hlsLocalDir;
        private String mp3S3Url;
        private String hlsS3Url;
        private String albumImageS3Url;
        private boolean success;
        private String errorMessage;

        public Builder songId(String songId) {
            this.songId = songId;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder artist(String artist) {
            this.artist = artist;
            return this;
        }

        public Builder duration(int duration) {
            this.duration = duration;
            return this;
        }

        public Builder mp3LocalPath(String mp3LocalPath) {
            this.mp3LocalPath = mp3LocalPath;
            return this;
        }

        public Builder hlsLocalDir(String hlsLocalDir) {
            this.hlsLocalDir = hlsLocalDir;
            return this;
        }

        public Builder mp3S3Url(String mp3S3Url) {
            this.mp3S3Url = mp3S3Url;
            return this;
        }

        public Builder hlsS3Url(String hlsS3Url) {
            this.hlsS3Url = hlsS3Url;
            return this;
        }

        public Builder albumImageS3Url(String albumImageS3Url) {
            this.albumImageS3Url = albumImageS3Url;
            return this;
        }

        public Builder success(boolean success) {
            this.success = success;
            return this;
        }

        public Builder errorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public SongProcessingResult build() {
            return new SongProcessingResult(this);
        }
    }
}
