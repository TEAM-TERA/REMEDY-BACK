package org.example.remedy.domain.song.service;

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
 * HLS (HTTP Live Streaming) 변환 서비스
 * MP3 파일을 HLS 형식으로 변환
 */
@Service
public class HLSService {

    private static final Logger logger = LoggerFactory.getLogger(HLSService.class);

    @Value("${app.hls.directory:./songs/hls}")
    private String hlsDirectory;

    @Value("${app.hls.segment-duration:10}")
    private int segmentDuration;

    /**
     * MP3 파일을 HLS 형식으로 변환
     *
     * @param mp3FilePath 입력 MP3 파일 경로
     * @param songId 곡 고유 ID (디렉토리명으로 사용)
     * @return HLS 플레이리스트 파일 경로
     */
    public String convertToHLS(String mp3FilePath, String songId) throws IOException, InterruptedException {
        logger.info("HLS 변환 시작: {} -> {}", mp3FilePath, songId);

        // 입력 파일 존재 확인
        File inputFile = new File(mp3FilePath);
        if (!inputFile.exists()) {
            throw new RuntimeException("입력 MP3 파일을 찾을 수 없습니다: " + mp3FilePath);
        }

        // HLS 출력 디렉토리 생성
        Path hlsPath = Paths.get(hlsDirectory, songId);
        Files.createDirectories(hlsPath);

        String playlistPath = hlsPath.resolve("playlist.m3u8").toString();
        String segmentPattern = hlsPath.resolve("segment%03d.ts").toString();

        // FFmpeg 명령어 구성
        ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg",
                "-i", mp3FilePath,                    // 입력 파일
                "-c:a", "aac",                        // 오디오 코덱: AAC
                "-b:a", "128k",                       // 비트레이트: 128kbps
                "-f", "hls",                          // 출력 형식: HLS
                "-hls_time", String.valueOf(segmentDuration), // 세그먼트 길이
                "-hls_list_size", "0",                // 플레이리스트에 모든 세그먼트 포함
                "-hls_segment_filename", segmentPattern, // 세그먼트 파일명 패턴
                "-y",                                 // 기존 파일 덮어쓰기
                playlistPath                          // 출력 플레이리스트
        );

        pb.redirectErrorStream(true);
        Process process = pb.start();

        // 변환 대기 (최대 10분)
        boolean finished = process.waitFor(600, TimeUnit.SECONDS);
        if (!finished) {
            process.destroyForcibly();
            throw new RuntimeException("HLS 변환 타임아웃");
        }

        if (process.exitValue() != 0) {
            String error = new String(process.getInputStream().readAllBytes());
            throw new RuntimeException("HLS 변환 실패: " + error);
        }

        // 플레이리스트 파일 존재 확인
        File playlistFile = new File(playlistPath);
        if (!playlistFile.exists()) {
            throw new RuntimeException("HLS 플레이리스트 파일이 생성되지 않았습니다: " + playlistPath);
        }

        // 상대 경로로 반환 (웹 서버용)
        String relativePath = "/hls/" + songId + "/playlist.m3u8";

        logger.info("HLS 변환 완료: {}", relativePath);
        return relativePath;
    }
}