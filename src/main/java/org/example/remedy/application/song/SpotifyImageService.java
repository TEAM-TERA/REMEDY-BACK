package org.example.remedy.application.song;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.core5.util.Timeout;
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

    // HTTP 클라이언트 타임아웃 설정
    private final RequestConfig requestConfig = RequestConfig.custom()
            .setConnectionRequestTimeout(Timeout.ofSeconds(10))  // 커넥션 풀에서 연결 대기 시간
            .setConnectTimeout(Timeout.ofSeconds(10))            // 서버 연결 대기 시간
            .setResponseTimeout(Timeout.ofSeconds(30))           // 응답 대기 시간
            .build();

    public SpotifyAlbumImageResult searchAndDownloadAlbumImage(String songTitle, String artist) {
        long startTime = System.currentTimeMillis();

        try {
            // 토큰 확보 (타임아웃 적용)
            try {
                ensureValidTokenInternal();
            } catch (Exception e) {
                logger.error("Spotify 토큰 확보 실패: {} - {}, 기본값 반환", songTitle, artist, e);
                return SpotifyAlbumImageResult.builder().found(false).build();
            }

            String query = buildSearchQuery(songTitle, artist);
            logger.debug("Spotify 검색 쿼리: {}", query);

            // 검색 수행 (재시도 로직 포함)
            SpotifyTrackInfo trackInfo = null;
            try {
                trackInfo = searchTrackInfo(query);
            } catch (Exception e) {
                logger.warn("Spotify 검색 중 예외 발생: {} - {}, 기본값 반환", songTitle, artist, e);
                return SpotifyAlbumImageResult.builder().found(false).build();
            }

            if (trackInfo == null || trackInfo.getAlbumImageUrl() == null) {
                logger.debug("Spotify에서 트랙 정보를 찾을 수 없습니다: {} - {}", songTitle, artist);
                return SpotifyAlbumImageResult.builder()
                        .found(false)
                        .build();
            }

            // 이미지 다운로드 (재시도 로직 포함)
            String imagePath = null;
            try {
                imagePath = downloadImage(trackInfo.getAlbumImageUrl(), trackInfo.getTrackName(), trackInfo.getArtistName());
            } catch (Exception e) {
                logger.warn("Spotify 이미지 다운로드 실패: {} - {}, 메타데이터만 반환",
                           trackInfo.getTrackName(), trackInfo.getArtistName(), e);
                // 이미지 다운로드 실패해도 트랙 정보는 반환
                return SpotifyAlbumImageResult.builder()
                        .found(true)
                        .trackName(trackInfo.getTrackName())
                        .artistName(trackInfo.getArtistName())
                        .imageUrl(trackInfo.getAlbumImageUrl())
                        .localPath(null) // 이미지 경로는 null
                        .build();
            }

            long endTime = System.currentTimeMillis();
            logger.info("Spotify 검색 완료: {} -> {} by {}, 소요시간: {}ms",
                       songTitle, trackInfo.getTrackName(), trackInfo.getArtistName(), endTime - startTime);

            return SpotifyAlbumImageResult.builder()
                    .found(true)
                    .trackName(trackInfo.getTrackName())
                    .artistName(trackInfo.getArtistName())
                    .imageUrl(trackInfo.getAlbumImageUrl())
                    .localPath(imagePath)
                    .build();

        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            logger.error("Spotify 앨범 이미지 검색 예상치 못한 실패: {} - {}, 소요시간: {}ms",
                        songTitle, artist, endTime - startTime, e);
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
        int maxRetries = 3;
        IOException lastException = null;

        for (int retry = 0; retry < maxRetries; retry++) {
            try (CloseableHttpClient httpClient = HttpClients.custom()
                    .setDefaultRequestConfig(requestConfig)
                    .build()) {

                HttpPost post = new HttpPost("https://accounts.spotify.com/api/token");

                String credentials = Base64.getEncoder().encodeToString(
                        (clientId + ":" + clientSecret).getBytes(StandardCharsets.UTF_8)
                );
                post.setHeader("Authorization", "Basic " + credentials);
                post.setHeader("Content-Type", "application/x-www-form-urlencoded");
                post.setConfig(requestConfig); // 타임아웃 설정 적용

                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("grant_type", "client_credentials"));
                post.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));

                try (CloseableHttpResponse response = httpClient.execute(post)) {
                    if (response.getCode() >= 400) {
                        logger.warn("Spotify 토큰 갱신 실패 (시도 {}/{}): HTTP {}",
                                retry + 1, maxRetries, response.getCode());
                        if (retry == maxRetries - 1) {
                            throw new IOException("Spotify 토큰 갱신 실패: HTTP " + response.getCode());
                        }
                        continue;
                    }

                    String responseBody = EntityUtils.toString(response.getEntity());
                    JsonNode jsonResponse = objectMapper.readTree(responseBody);

                    this.accessToken = jsonResponse.get("access_token").asText();
                    int expiresIn = jsonResponse.get("expires_in").asInt();
                    this.tokenExpiryTime = System.currentTimeMillis() + (expiresIn * 1000L) - 60000; // 1분 여유

                    logger.info("Spotify 액세스 토큰 갱신 완료 (시도 {}/{})", retry + 1, maxRetries);
                    return; // 성공시 메서드 종료
                }

            } catch (IOException e) {
                lastException = e;
                logger.warn("Spotify 토큰 갱신 시도 실패 ({}/{}): {}",
                        retry + 1, maxRetries, e.getMessage());

                if (retry < maxRetries - 1) {
                    try {
                        Thread.sleep((retry + 1) * 1000); // 지수 백오프: 1초, 2초, 3초
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new IOException("토큰 갱신 중 인터럽트 발생", ie);
                    }
                }
            }
        }

        // 모든 재시도 실패
        throw new IOException("Spotify 토큰 갱신 최대 재시도 실패", lastException);
    }

    private String buildSearchQuery(String songTitle, String artist) {
        String cleanTitle = songTitle.replaceAll("\\[.*?\\]|\\(.*?\\)|Official|Music|Video|MV", "").trim();
        String cleanArtist = artist.replaceAll("\\s*-\\s*Topic", "").trim();
        return cleanTitle + " " + cleanArtist;
    }

    private SpotifyTrackInfo searchTrackInfo(String query) throws IOException, ParseException {
        int maxRetries = 2;
        IOException lastException = null;

        for (int retry = 0; retry < maxRetries; retry++) {
            try (CloseableHttpClient httpClient = HttpClients.custom()
                    .setDefaultRequestConfig(requestConfig)
                    .build()) {

                String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
                String url = "https://api.spotify.com/v1/search?q=" + encodedQuery + "&type=track&limit=1";

                HttpGet get = new HttpGet(url);
                get.setHeader("Authorization", "Bearer " + accessToken);
                get.setConfig(requestConfig); // 타임아웃 설정 적용

                try (CloseableHttpResponse response = httpClient.execute(get)) {
                    // HTTP 응답 상태 확인
                    if (response.getCode() == 401) {
                        logger.warn("Spotify API 인증 만료, 토큰 갱신 후 재시도");
                        refreshAccessToken();
                        if (retry < maxRetries - 1) {
                            continue; // 토큰 갱신 후 재시도
                        }
                    }

                    if (response.getCode() >= 400) {
                        logger.warn("Spotify API 검색 실패 (시도 {}/{}): HTTP {} for query: {}",
                                retry + 1, maxRetries, response.getCode(), query);
                        if (retry == maxRetries - 1) {
                            return null; // 검색 실패시 null 반환 (예외 대신)
                        }
                        continue;
                    }

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

                        logger.debug("Spotify 검색 성공 (시도 {}/{}): {}", retry + 1, maxRetries, query);
                        return SpotifyTrackInfo.builder()
                                .trackName(track.path("name").asText())
                                .artistName(artistName)
                                .albumImageUrl(imageUrl)
                                .build();
                    }

                    // 검색 결과 없음
                    logger.debug("Spotify 검색 결과 없음: {}", query);
                    return null;
                }

            } catch (IOException e) {
                lastException = e;
                logger.warn("Spotify API 검색 시도 실패 ({}/{}): {} for query: {}",
                        retry + 1, maxRetries, e.getMessage(), query);

                if (retry < maxRetries - 1) {
                    try {
                        Thread.sleep((retry + 1) * 500); // 짧은 대기: 0.5초, 1초
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        logger.warn("Spotify 검색 중 인터럽트 발생");
                        return null;
                    }
                }
            }
        }

        // 모든 재시도 실패
        logger.error("Spotify API 검색 최대 재시도 실패: {} - {}", query,
                lastException != null ? lastException.getMessage() : "Unknown error");
        return null; // 예외 대신 null 반환하여 전체 배치 중단 방지
    }

    private String downloadImage(String imageUrl, String songTitle, String artist) throws IOException {
        Path albumImagesPath = Paths.get(albumImagesDirectory);
        Files.createDirectories(albumImagesPath);

        String sanitizedFileName = sanitizeFileName(songTitle + "_" + artist) + ".jpg";
        Path imagePath = albumImagesPath.resolve(sanitizedFileName);

        int maxRetries = 2;
        IOException lastException = null;

        for (int retry = 0; retry < maxRetries; retry++) {
            try (CloseableHttpClient httpClient = HttpClients.custom()
                    .setDefaultRequestConfig(requestConfig)
                    .build()) {

                HttpGet get = new HttpGet(imageUrl);
                get.setConfig(requestConfig); // 타임아웃 설정 적용

                try (CloseableHttpResponse response = httpClient.execute(get)) {
                    if (response.getCode() >= 400) {
                        logger.warn("이미지 다운로드 실패 (시도 {}/{}): HTTP {} for {}",
                                retry + 1, maxRetries, response.getCode(), imageUrl);
                        if (retry == maxRetries - 1) {
                            throw new IOException("이미지 다운로드 실패: HTTP " + response.getCode());
                        }
                        continue;
                    }

                    try (InputStream inputStream = response.getEntity().getContent()) {
                        Files.copy(inputStream, imagePath, StandardCopyOption.REPLACE_EXISTING);
                        logger.info("앨범 이미지 다운로드 완료 (시도 {}/{}): {}", retry + 1, maxRetries, imagePath);
                        return imagePath.toString();
                    }
                }

            } catch (IOException e) {
                lastException = e;
                logger.warn("이미지 다운로드 시도 실패 ({}/{}): {} for {}",
                        retry + 1, maxRetries, e.getMessage(), imageUrl);

                if (retry < maxRetries - 1) {
                    try {
                        Thread.sleep((retry + 1) * 500); // 0.5초, 1초 대기
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new IOException("이미지 다운로드 중 인터럽트 발생", ie);
                    }
                }
            }
        }

        // 모든 재시도 실패
        throw new IOException("이미지 다운로드 최대 재시도 실패: " + imageUrl, lastException);
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