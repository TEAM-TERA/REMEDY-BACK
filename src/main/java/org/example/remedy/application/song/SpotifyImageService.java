package org.example.remedy.application.song;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class SpotifyImageService {

    private static final Logger logger = LoggerFactory.getLogger(SpotifyImageService.class);

    @Value("${spotify.client.id}")
    private String clientId;

    @Value("${spotify.client.secret}")
    private String clientSecret;

    @Value("${app.album.images.directory:./songs/album-images}")
    private String albumImagesDirectory;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private String accessToken;
    private long tokenExpiryTime;

    public SpotifyAlbumImageResult searchAndDownloadAlbumImage(String songTitle, String artist) {
        try {
            ensureValidTokenInternal();

            String query = buildSearchQuery(songTitle, artist);
            SpotifyTrackInfo trackInfo = searchTrackInfo(query);

            if (trackInfo == null || trackInfo.getAlbumImageUrl() == null) {
                logger.warn("Spotify에서 트랙 정보를 찾을 수 없습니다: {} - {}", songTitle, artist);
                return SpotifyAlbumImageResult.builder()
                        .found(false)
                        .build();
            }

            String imagePath = downloadImage(trackInfo.getAlbumImageUrl(), trackInfo.getTrackName(), trackInfo.getArtistName());

            return SpotifyAlbumImageResult.builder()
                    .found(true)
                    .trackName(trackInfo.getTrackName())
                    .artistName(trackInfo.getArtistName())
                    .imageUrl(trackInfo.getAlbumImageUrl())
                    .localPath(imagePath)
                    .build();

        } catch (Exception e) {
            logger.error("Spotify 앨범 이미지 검색 실패: {} - {}", songTitle, artist, e);
            return SpotifyAlbumImageResult.builder()
                    .found(false)
                    .build();
        }
    }

    public void ensureValidToken() throws IOException, ParseException {
        if (accessToken == null || System.currentTimeMillis() >= tokenExpiryTime) {
            refreshAccessToken();
        }
    }

    private void ensureValidTokenInternal() throws IOException, ParseException {
        ensureValidToken();
    }

    private void refreshAccessToken() throws IOException, ParseException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost post = new HttpPost("https://accounts.spotify.com/api/token");

            String credentials = Base64.getEncoder().encodeToString(
                    (clientId + ":" + clientSecret).getBytes(StandardCharsets.UTF_8)
            );
            post.setHeader("Authorization", "Basic " + credentials);
            post.setHeader("Content-Type", "application/x-www-form-urlencoded");

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("grant_type", "client_credentials"));
            post.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));

            try (CloseableHttpResponse response = httpClient.execute(post)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                JsonNode jsonResponse = objectMapper.readTree(responseBody);

                this.accessToken = jsonResponse.get("access_token").asText();
                int expiresIn = jsonResponse.get("expires_in").asInt();
                this.tokenExpiryTime = System.currentTimeMillis() + (expiresIn * 1000L) - 60000; // 1분 여유

                logger.info("Spotify 액세스 토큰 갱신 완료");
            }
        }
    }

    private String buildSearchQuery(String songTitle, String artist) {
        String cleanTitle = songTitle.replaceAll("\\[.*?\\]|\\(.*?\\)|Official|Music|Video|MV", "").trim();
        String cleanArtist = artist.replaceAll("\\s*-\\s*Topic", "").trim();
        return cleanTitle + " " + cleanArtist;
    }

    private SpotifyTrackInfo searchTrackInfo(String query) throws IOException, ParseException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String url = "https://api.spotify.com/v1/search?q=" + encodedQuery + "&type=track&limit=1";

            HttpGet get = new HttpGet(url);
            get.setHeader("Authorization", "Bearer " + accessToken);

            try (CloseableHttpResponse response = httpClient.execute(get)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                JsonNode jsonResponse = objectMapper.readTree(responseBody);

                JsonNode tracks = jsonResponse.path("tracks").path("items");
                if (tracks.size() > 0) {
                    JsonNode track = tracks.get(0);
                    JsonNode album = track.path("album");
                    JsonNode images = album.path("images");
                    JsonNode artists = track.path("artists");

                    String imageUrl = null;
                    if (images.size() > 0) {
                        imageUrl = images.get(0).path("url").asText();
                    }

                    String artistName = "";
                    if (artists.size() > 0) {
                        artistName = artists.get(0).path("name").asText();
                    }

                    return SpotifyTrackInfo.builder()
                            .trackName(track.path("name").asText())
                            .artistName(artistName)
                            .albumImageUrl(imageUrl)
                            .build();
                }

                return null;
            }
        }
    }

    private String downloadImage(String imageUrl, String songTitle, String artist) throws IOException {
        Path albumImagesPath = Paths.get(albumImagesDirectory);
        Files.createDirectories(albumImagesPath);

        String sanitizedFileName = sanitizeFileName(songTitle + "_" + artist) + ".jpg";
        Path imagePath = albumImagesPath.resolve(sanitizedFileName);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet get = new HttpGet(imageUrl);

            try (CloseableHttpResponse response = httpClient.execute(get);
                 InputStream inputStream = response.getEntity().getContent()) {

                Files.copy(inputStream, imagePath, StandardCopyOption.REPLACE_EXISTING);
                logger.info("앨범 이미지 다운로드 완료: {}", imagePath);

                return imagePath.toString();
            }
        }
    }

    private String sanitizeFileName(String fileName) {
        return fileName.replaceAll("[^a-zA-Z0-9가-힣\\s\\-_]", "")
                .replaceAll("\\s+", "_")
                .substring(0, Math.min(fileName.length(), 100));
    }

    public static class SpotifyTrackInfo {
        private String trackName;
        private String artistName;
        private String albumImageUrl;

        public static SpotifyTrackInfoBuilder builder() {
            return new SpotifyTrackInfoBuilder();
        }

        public static class SpotifyTrackInfoBuilder {
            private String trackName;
            private String artistName;
            private String albumImageUrl;

            public SpotifyTrackInfoBuilder trackName(String trackName) {
                this.trackName = trackName;
                return this;
            }

            public SpotifyTrackInfoBuilder artistName(String artistName) {
                this.artistName = artistName;
                return this;
            }

            public SpotifyTrackInfoBuilder albumImageUrl(String albumImageUrl) {
                this.albumImageUrl = albumImageUrl;
                return this;
            }

            public SpotifyTrackInfo build() {
                SpotifyTrackInfo result = new SpotifyTrackInfo();
                result.trackName = this.trackName;
                result.artistName = this.artistName;
                result.albumImageUrl = this.albumImageUrl;
                return result;
            }
        }

        public String getTrackName() { return trackName; }
        public String getArtistName() { return artistName; }
        public String getAlbumImageUrl() { return albumImageUrl; }
    }

    public static class SpotifyAlbumImageResult {
        private boolean found;
        private String trackName;
        private String artistName;
        private String imageUrl;
        private String localPath;

        public static SpotifyAlbumImageResultBuilder builder() {
            return new SpotifyAlbumImageResultBuilder();
        }

        public static class SpotifyAlbumImageResultBuilder {
            private boolean found;
            private String trackName;
            private String artistName;
            private String imageUrl;
            private String localPath;

            public SpotifyAlbumImageResultBuilder found(boolean found) {
                this.found = found;
                return this;
            }

            public SpotifyAlbumImageResultBuilder trackName(String trackName) {
                this.trackName = trackName;
                return this;
            }

            public SpotifyAlbumImageResultBuilder artistName(String artistName) {
                this.artistName = artistName;
                return this;
            }

            public SpotifyAlbumImageResultBuilder imageUrl(String imageUrl) {
                this.imageUrl = imageUrl;
                return this;
            }

            public SpotifyAlbumImageResultBuilder localPath(String localPath) {
                this.localPath = localPath;
                return this;
            }

            public SpotifyAlbumImageResult build() {
                SpotifyAlbumImageResult result = new SpotifyAlbumImageResult();
                result.found = this.found;
                result.trackName = this.trackName;
                result.artistName = this.artistName;
                result.imageUrl = this.imageUrl;
                result.localPath = this.localPath;
                return result;
            }
        }

        public boolean isFound() { return found; }
        public String getTrackName() { return trackName; }
        public String getArtistName() { return artistName; }
        public String getImageUrl() { return imageUrl; }
        public String getLocalPath() { return localPath; }
    }
}