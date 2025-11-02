package org.example.remedy.application.song;

import lombok.RequiredArgsConstructor;
import org.example.remedy.application.storage.port.out.StoragePort;
import org.example.remedy.infrastructure.storage.s3.S3StorageAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

/**
 * HLS (HTTP Live Streaming) 변환 서비스
 * MP3 파일을 HLS 형식으로 변환하고 S3에 업로드
 */
@Service
@RequiredArgsConstructor
public class HLSService {

    private static final Logger logger = LoggerFactory.getLogger(HLSService.class);

    private final StoragePort storagePort;

    @Value("${app.hls.segment-duration:10}")
    private int segmentDuration;

    @Value("${app.hls.directory:./hls}")
    private String hlsDirectory;

    /**
     * MP3 파일을 HLS 형식으로 변환 (로컬만, S3 업로드 없음)
     *
     * @param mp3FilePath 입력 MP3 파일 경로
     * @param songId 곡 고유 ID (디렉토리명으로 사용)
     * @return 로컬 HLS 디렉토리 경로
     */
    public String convertToHLSLocal(String mp3FilePath, String songId) throws IOException, InterruptedException {
        logger.info("HLS 변환 시작: {} -> {}", mp3FilePath, songId);

        // 입력 파일 존재 확인
        File inputFile = new File(mp3FilePath);
        if (!inputFile.exists()) {
            throw new RuntimeException("입력 MP3 파일을 찾을 수 없습니다: " + mp3FilePath);
        }

        // 설정된 HLS 디렉토리 사용
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

        logger.info("HLS 로컬 변환 완료: {}", hlsPath.toAbsolutePath());
        return hlsPath.toAbsolutePath().toString();
    }

    /**
     * HLS 파일들을 S3에 업로드
     * @param hlsDir HLS 파일들이 있는 로컬 디렉토리
     * @param songId 곡 ID
     * @return S3 플레이리스트 URL
     */
    public String uploadHLSFilesToS3(File hlsDir, String songId) throws IOException {
        File[] files = hlsDir.listFiles();
        if (files == null || files.length == 0) {
            throw new RuntimeException("HLS 파일을 찾을 수 없습니다: " + hlsDir.getAbsolutePath());
        }

        String playlistS3Url = null;

        // 모든 파일 업로드 (playlist.m3u8 + segment*.ts)
        for (File file : files) {
            String fileName = file.getName();
            String s3Key = "hls/" + songId + "/" + fileName;

            try (FileInputStream inputStream = new FileInputStream(file)) {
                String contentType = fileName.endsWith(".m3u8") ? "application/vnd.apple.mpegurl" : "video/MP2T";

                String s3Url = storagePort.uploadFile(
                        inputStream,
                        s3Key,
                        contentType,
                        file.length()
                );

                logger.info("HLS 파일 S3 업로드: {} -> {}", fileName, s3Url);

                // playlist.m3u8 URL 저장
                if (fileName.equals("playlist.m3u8")) {
                    playlistS3Url = s3Url;
                }
            }
        }

        if (playlistS3Url == null) {
            throw new RuntimeException("playlist.m3u8 파일 업로드 실패");
        }

        // 업로드 완료 후 로컬 파일 삭제
        deleteLocalHLSFiles(hlsDir);

        return playlistS3Url;
    }

    /**
     * 로컬 HLS 파일들 삭제
     */
    private void deleteLocalHLSFiles(File hlsDir) {
        try {
            File[] files = hlsDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.delete()) {
                        logger.debug("로컬 HLS 파일 삭제: {}", file.getName());
                    }
                }
            }
            if (hlsDir.delete()) {
                logger.info("로컬 HLS 디렉토리 삭제: {}", hlsDir.getAbsolutePath());
            }
        } catch (Exception e) {
            logger.warn("로컬 HLS 파일 삭제 실패: {}", hlsDir.getAbsolutePath(), e);
        }
    }
}