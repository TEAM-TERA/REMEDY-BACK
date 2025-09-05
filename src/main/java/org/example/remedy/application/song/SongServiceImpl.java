package org.example.remedy.application.song;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.remedy.application.song.exception.SongNotFoundException;
import org.example.remedy.application.song.port.in.SongService;
import org.example.remedy.domain.song.Song;
import org.example.remedy.global.util.mapper.Mapper;
import org.example.remedy.infrastructure.persistence.song.SongCustomRepository;
import org.example.remedy.application.song.port.out.SongRepository;
import org.example.remedy.presentation.song.dto.YouTubeMetadata;
import org.example.remedy.presentation.song.dto.request.SongCreateRequest;
import org.example.remedy.application.song.dto.response.SongListResponse;
import org.example.remedy.application.song.dto.response.SongResponse;
import org.example.remedy.application.song.dto.response.SongSearchListResponse;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * 간소화된 노래 서비스
 * 검색 로직은 SongQueryBuilder로 분리
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SongServiceImpl implements SongService {
    private final YouTubeService youTubeService;
    private final HLSService hlsService;
    private final SongCustomRepository songCustomRepository;  // 검색 로직 위임
    private final SongRepository songRepository;

    /**
     * 노래 추가 (YouTube → MP3 → HLS → DB 저장)
     */
    @Transactional
    public SongListResponse createSongs(SongCreateRequest request) {
        List<Song> songs = new ArrayList<>();

        for (String title : request.titles()) {
            try {
                YouTubeMetadata metadata = youTubeService.extractMetadata(title);
                String safeFilename = youTubeService.createSafeFilename(metadata.getTitle());
                String mp3FilePath = youTubeService.downloadMP3(title, safeFilename);
                String hlsPath = hlsService.convertToHLS(mp3FilePath, safeFilename);
                Song song = Song.newInstance(metadata, hlsPath);
                songs.add(song);
            } catch (Exception e) {
                log.error("노래 추가 실패: {}", title, e);
                // 실패한 곡은 건너뛰고 계속 진행
            }
        }

        // songRepository의 Iterable<Song> 반환값을 Mapper 클래스의 toList()로 List로 변환
        songs = Mapper.toList(songRepository.saveAll(songs));

        return SongListResponse.newInstanceBySongs(songs);
    }

    /**
     * ID로 곡 조회
     */
    public SongResponse getSongById(String id) {
        Song song = songRepository.findById(id)
                .orElseThrow(SongNotFoundException::new);
        return SongResponse.newInstance(song);
    }

    /**
     * 유사도 기반 통합 검색 (SongQueryBuilder 위임)
     */
    public SongSearchListResponse searchSongs(String query) {
        List<Song> songs = songCustomRepository.searchSongs(query);
        return SongSearchListResponse.newInstanceBySongList(songs);
    }

    /**
     * 모든 곡 조회 (관리자용)
     */
    public SongListResponse getAllSongs() {
        // songRepository의 Iterable<Song> 반환값을 Mapper 클래스의 toList()로 List로 변환
        List<Song> songs = Mapper.toList(songRepository.findAll());
        return SongListResponse.newInstanceBySongs(songs);
    }

    public ResponseEntity<Resource> streamSong(String title) throws IOException {
        // 1. 곡 정보 조회
        Song song = songRepository.findByTitle(title)
                .orElseThrow(SongNotFoundException::new);

        // 2. MP3 파일 경로 구성 (안전한 파일명 사용)
        String safeFileName = song.getTitle()
                .replaceAll("[^a-zA-Z0-9가-힣\\s]", "_")  // 특수문자 제거
                .trim();
        String mp3FilePath = "songs/music/" + safeFileName + ".mp3";
        Path filePath = Paths.get(mp3FilePath);

        // 3. 파일 존재 확인
        if (!Files.exists(filePath)) {
            log.error("MP3 파일을 찾을 수 없습니다: {}", mp3FilePath);
            throw new SongNotFoundException();
        }

        // 4. 파일 리소스 생성
        Resource resource = new UrlResource(filePath.toUri());

        // 5. 파일 크기 계산
        long fileSize = Files.size(filePath);

        // 6. HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "audio/mpeg");
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + song.getTitle() + ".mp3\"");
        headers.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileSize));
        headers.add(HttpHeaders.ACCEPT_RANGES, "bytes");  // Range 요청 지원
        headers.add(HttpHeaders.CACHE_CONTROL, "public, max-age=3600");  // 캐시 허용
        headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");

        // 7. ResponseEntity 반환
        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }
}