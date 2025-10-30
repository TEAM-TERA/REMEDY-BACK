package org.example.remedy.application.song;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class YouTubeDownloadService {

    private static final Logger logger = LoggerFactory.getLogger(YouTubeDownloadService.class);

    @Value("${youtube.api.key}")
    private String youtubeApiKey;

    @Value("${app.download.directory}")
    private String downloadDirectory;

    private final YouTube youTube;

    public YouTubeDownloadService() {
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

    public YouTubeSearchResult searchAndDownload(String songTitle) {
        try {
            String videoId = searchVideoByTitle(songTitle);
            if (videoId == null) {
                throw new RuntimeException("노래를 찾을 수 없습니다: " + songTitle);
            }

            Video videoDetails = getVideoDetails(videoId);
            String downloadPath = downloadAudio(videoId, songTitle);

            return YouTubeSearchResult.builder()
                    .videoId(videoId)
                    .title(videoDetails.getSnippet().getTitle())
                    .channelTitle(videoDetails.getSnippet().getChannelTitle())
                    .duration(parseDuration(videoDetails.getContentDetails().getDuration()))
                    .downloadPath(downloadPath)
                    .build();

        } catch (Exception e) {
            logger.error("YouTube 다운로드 실패: {}", songTitle, e);
            throw new RuntimeException("YouTube 다운로드 실패: " + songTitle, e);
        }
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
            return null;
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
        Path downloadPath = Paths.get(downloadDirectory);
        Files.createDirectories(downloadPath);

        String sanitizedTitle = sanitizeFileName(songTitle);
        String outputPath = downloadPath.resolve(sanitizedTitle + ".%(ext)s").toString();
        String cookiePath = "/cookies.txt";
        String finalPath = downloadPath.resolve(sanitizedTitle + ".mp3").toString();

        ProcessBuilder pb = new ProcessBuilder(
                "yt-dlp",
                "--cookies", cookiePath,
                "--extract-audio",
                "--audio-format", "mp3",
                "--audio-quality", "192K",
                "--output", outputPath,
                "--no-playlist",
                "https://www.youtube.com/watch?v=" + videoId
        );

        pb.redirectErrorStream(true);
        Process process = pb.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                logger.info("yt-dlp: {}", line);
            }
        }

        boolean finished = process.waitFor(600, TimeUnit.SECONDS);
        if (!finished) {
            process.destroyForcibly();
            throw new RuntimeException("다운로드 타임아웃: " + songTitle);
        }

        if (process.exitValue() != 0) {
            throw new RuntimeException("다운로드 실패: " + songTitle);
        }

        File downloadedFile = new File(finalPath);
        if (!downloadedFile.exists()) {
            throw new RuntimeException("다운로드된 파일을 찾을 수 없습니다: " + finalPath);
        }

        return finalPath;
    }

    private String sanitizeFileName(String fileName) {
        return fileName.replaceAll("[^a-zA-Z0-9가-힣\\s\\-_]", "")
                .replaceAll("\\s+", "_")
                .substring(0, Math.min(fileName.length(), 100));
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

        public static YouTubeSearchResultBuilder builder() {
            return new YouTubeSearchResultBuilder();
        }

        public static class YouTubeSearchResultBuilder {
            private String videoId;
            private String title;
            private String channelTitle;
            private int duration;
            private String downloadPath;

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

            public YouTubeSearchResult build() {
                YouTubeSearchResult result = new YouTubeSearchResult();
                result.videoId = this.videoId;
                result.title = this.title;
                result.channelTitle = this.channelTitle;
                result.duration = this.duration;
                result.downloadPath = this.downloadPath;
                return result;
            }
        }

        public String getVideoId() { return videoId; }
        public String getTitle() { return title; }
        public String getChannelTitle() { return channelTitle; }
        public int getDuration() { return duration; }
        public String getDownloadPath() { return downloadPath; }
    }
}