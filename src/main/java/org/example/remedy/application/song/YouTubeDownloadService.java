package org.example.remedy.application.song;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import org.example.remedy.application.storage.port.out.StoragePort;
import org.example.remedy.infrastructure.storage.s3.S3StorageAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class YouTubeDownloadService {
    private static final Logger logger = LoggerFactory.getLogger(YouTubeDownloadService.class);

    private final StoragePort storagePort;

    @Value("${youtube.api.key}")
    private String youtubeApiKey;

    private final YouTube youTube;

    public YouTubeDownloadService(S3StorageAdapter s3StorageAdapter) {
        this.storagePort = s3StorageAdapter;
        try {
            this.youTube = new YouTube.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance(),
                    null
            ).setApplicationName("REMEDY-BACK").build();
        } catch (GeneralSecurityException | IOException e) {
            logger.error("YouTube API 초기화 실패", e);
            throw new RuntimeException("YouTube API 초기화 실패", e);
        }
    }

    public YouTubeSearchResult searchAndDownload(String songTitle) throws IOException, InterruptedException {
        String videoId = searchVideoByTitle(songTitle);

        Video videoDetails = getVideoDetails(videoId);
        String downloadPath = downloadAudio(videoId, songTitle);

        return YouTubeSearchResult.builder()
                .videoId(videoId)
                .title(videoDetails.getSnippet().getTitle())
                .channelTitle(videoDetails.getSnippet().getChannelTitle())
                .duration(parseDuration(videoDetails.getContentDetails().getDuration()))
                .downloadPath(downloadPath)
                .build();
    }

    private String searchVideoByTitle(String title) throws IOException {
        YouTube.Search.List search = youTube.search().list(List.of("id", "snippet"));
        search.setKey(youtubeApiKey);
        search.setQ(title + " audio");
        search.setType(List.of("video"));
        search.setMaxResults(1L);
        search.setOrder("relevance");

        SearchListResponse searchResponse = search.execute();
        List<SearchResult> searchResults = searchResponse.getItems();

        if (searchResults.isEmpty()) {
            throw new RuntimeException("노래를 찾을 수 없습니다. 제목 : " + title);
        }

        return searchResults.getFirst().getId().getVideoId();
    }

    private Video getVideoDetails(String videoId) throws IOException {
        YouTube.Videos.List videoRequest = youTube.videos().list(List.of("snippet", "contentDetails"));
        videoRequest.setKey(youtubeApiKey);
        videoRequest.setId(List.of(videoId));

        VideoListResponse videoResponse = videoRequest.execute();
        List<Video> videos = videoResponse.getItems();

        if (videos.isEmpty()) {
            throw new RuntimeException("비디오 정보를 가져올 수 없습니다: " + videoId);
        }

        return videos.getFirst();
    }

    private String downloadAudio(String videoId, String songTitle) throws IOException, InterruptedException {
        Path downloadPath = Paths.get("./songs");
        Files.createDirectories(downloadPath);

        String outputPath = downloadPath.resolve(songTitle + ".%(ext)s").toString();
        String finalPath = downloadPath.resolve(songTitle + ".mp3").toString();

        ProcessBuilder processBuilder = new ProcessBuilder(
                "yt-dlp",
                "--extract-audio",
                "--audio-format", "mp3",
                "--audio-quality", "192K",
                "--output", outputPath,
                "--no-playlist",
                "https://www.youtube.com/watch?v=" + videoId
        );

        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        try (ExecutorService executor = Executors.newSingleThreadExecutor()) {
            executor.submit(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        logger.info("yt-dlp: {}", line);
                    }
                } catch (IOException e) {
                    logger.error("yt-dlp 로그 읽기 실패", e);
                }
            });

            boolean finished = process.waitFor(60, TimeUnit.SECONDS);
            executor.shutdown();

            if (!finished) {
                process.destroyForcibly();
                throw new RuntimeException("다운로드 타임아웃: " + songTitle);
            }

            if (process.exitValue() != 0) {
                throw new RuntimeException("다운로드 실패: " + songTitle);
            }

            return finalPath;
        }
    }

    /**
     * MP3 파일을 S3에 업로드
     */
    public String uploadToS3(String mp3FilePath, String songId) throws IOException {
        logger.info("S3 업로드 시작: {}", mp3FilePath);

        File mp3File = new File(mp3FilePath);
        if (!mp3File.exists()) {
            throw new RuntimeException("MP3 파일을 찾을 수 없습니다: " + mp3FilePath);
        }

        String s3Key = "songs/" + songId + ".mp3";

        try (FileInputStream inputStream = new FileInputStream(mp3File)) {
            String s3Url = storagePort.uploadFile(
                    inputStream,
                    s3Key,
                    "audio/mpeg",
                    mp3File.length()
            );

            logger.info("S3 업로드 완료: {}", s3Url);
            return s3Url;
        }
    }

    private int parseDuration(String isoDuration) {
        if (isoDuration == null || !isoDuration.startsWith("PT")) {
            return 0;
        }

        int totalSeconds = 0;
        String duration = isoDuration.substring(2);

        if (duration.contains("H")) {
            int hours = Integer.parseInt(duration.substring(0, duration.indexOf("H")));
            totalSeconds += hours * 3600;
            duration = duration.substring(duration.indexOf("H") + 1);
        }

        if (duration.contains("M")) {
            int minutes = Integer.parseInt(duration.substring(0, duration.indexOf("M")));
            totalSeconds += minutes * 60;
            duration = duration.substring(duration.indexOf("M") + 1);
        }

        if (duration.contains("S")) {
            int seconds = Integer.parseInt(duration.substring(0, duration.indexOf("S")));
            totalSeconds += seconds;
        }

        return totalSeconds;
    }

    public static class YouTubeSearchResult {
        private String videoId;
        private String title;
        private String channelTitle;
        private int duration;
        private String downloadPath;
        private String s3Url;

        public static YouTubeSearchResultBuilder builder() {
            return new YouTubeSearchResultBuilder();
        }

        public static class YouTubeSearchResultBuilder {
            private String videoId;
            private String title;
            private String channelTitle;
            private int duration;
            private String downloadPath;
            private String s3Url;

            public YouTubeSearchResultBuilder videoId(String videoId) {
                this.videoId = videoId;
                return this;
            }

            public YouTubeSearchResultBuilder title(String title) {
                this.title = title;
                return this;
            }

            public YouTubeSearchResultBuilder channelTitle(String channelTitle) {
                this.channelTitle = channelTitle;
                return this;
            }

            public YouTubeSearchResultBuilder duration(int duration) {
                this.duration = duration;
                return this;
            }

            public YouTubeSearchResultBuilder downloadPath(String downloadPath) {
                this.downloadPath = downloadPath;
                return this;
            }

            public YouTubeSearchResultBuilder s3Url(String s3Url) {
                this.s3Url = s3Url;
                return this;
            }

            public YouTubeSearchResult build() {
                YouTubeSearchResult result = new YouTubeSearchResult();
                result.videoId = this.videoId;
                result.title = this.title;
                result.channelTitle = this.channelTitle;
                result.duration = this.duration;
                result.downloadPath = this.downloadPath;
                result.s3Url = this.s3Url;
                return result;
            }
        }

        public String getVideoId() { return videoId; }
        public String getTitle() { return title; }
        public String getChannelTitle() { return channelTitle; }
        public int getDuration() { return duration; }
        public String getDownloadPath() { return downloadPath; }
        public String getS3Url() { return s3Url; }
    }
}