package org.example.remedy.application.song;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.remedy.interfaces.song.dto.YouTubeMetadata;
import org.example.remedy.application.song.exception.MetadataNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

/**
 * YouTube 다운로드 및 메타데이터 추출 서비스
 */
@Service
@RequiredArgsConstructor
public class YouTubeService {
    private static final Logger logger = LoggerFactory.getLogger(YouTubeService.class);
    private final ObjectMapper objectMapper;

    @Value("${app.music.directory:./songs/music}")
    private String musicDirectory;

    /**
     * YouTube에서 메타데이터 추출
     */
    public YouTubeMetadata extractMetadata(String title) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(
                "yt-dlp",
                "--cookies-from-browser", "chrome",
                "--dump-json",
                "--no-download",
                "ytsearch1:" + title
        );

        pb.redirectErrorStream(true);
        Process process = pb.start();

        boolean finished = process.waitFor(30, TimeUnit.SECONDS);
        if (!finished) {
            process.destroyForcibly();
            throw new RuntimeException("메타데이터 추출 타임아웃");
        }

        if (process.exitValue() != 0) {
            throw new RuntimeException("yt-dlp 메타데이터 추출 실패");
        }

        // JSON 파싱
        String output = new String(process.getInputStream().readAllBytes());
        JsonNode jsonNode = objectMapper.readTree(output);
        if (jsonNode == null || jsonNode.isNull()) {
            throw new MetadataNotFoundException();
        }
        YouTubeMetadata metadata = YouTubeMetadata.newInstance(jsonNode);

        logger.info("메타데이터 추출 완료: {}", metadata);
        return metadata;
    }

    /**
     * YouTube에서 MP3 다운로드
     */
    public String downloadMP3(String searchQuery, String filename) throws IOException, InterruptedException {
        logger.info("YouTube MP3 다운로드 시작: {}", searchQuery);

        // 디렉토리 생성
        Path musicPath = Paths.get(musicDirectory);
        Files.createDirectories(musicPath);

        String outputPath = musicPath.resolve(filename + ".mp3").toString();

        ProcessBuilder pb = new ProcessBuilder(
                "yt-dlp",
                "--cookies-from-browser", "chrome",
                "-x", "--audio-format", "mp3",
                "--output", outputPath,
                "ytsearch1:" + searchQuery
        );

        pb.redirectErrorStream(true);
        Process process = pb.start();

        boolean finished = process.waitFor(30, TimeUnit.SECONDS); // 30초 타임아웃
        if (!finished) {
            process.destroyForcibly();
            throw new RuntimeException("MP3 다운로드 타임아웃");
        }

        if (process.exitValue() != 0) {
            String error = new String(process.getInputStream().readAllBytes());
            throw new RuntimeException("MP3 다운로드 실패: " + error);
        }

        // 파일 존재 확인
        File downloadedFile = new File(outputPath);
        if (!downloadedFile.exists()) {
            throw new RuntimeException("다운로드된 파일을 찾을 수 없습니다: " + outputPath);
        }

        logger.info("MP3 다운로드 완료: {}", outputPath);
        return outputPath;
    }

    public String createSafeFilename(String title) {
        if (title == null || title.trim().isEmpty()) {
            return "unknown_" + System.currentTimeMillis();
        }

        // 특수문자 제거 및 공백을 언더스코어로 변경
        String safe = title.replaceAll("[\\\\/:*?\"<>|]", "")
                .replaceAll("\\s+", "_")
                .replaceAll("_{2,}", "_")
                .trim();

        // 길이 제한 (파일시스템 제한 고려)
        if (safe.length() > 100) {
            safe = safe.substring(0, 100);
        }
        return safe;
    }
}