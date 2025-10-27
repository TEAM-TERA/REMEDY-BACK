package org.example.remedy.application.song;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.remedy.application.song.exception.SongNotFoundException;
import org.example.remedy.application.song.port.in.SongService;
import org.example.remedy.application.song.port.out.SongPersistencePort;
import org.example.remedy.domain.song.Song;
import org.example.remedy.global.util.mapper.Mapper;
import org.example.remedy.infrastructure.persistence.song.SongCustomRepository;
import org.example.remedy.application.song.dto.response.SongListResponse;
import org.example.remedy.application.song.dto.response.SongResponse;
import org.example.remedy.application.song.dto.response.SongSearchListResponse;
import org.springframework.beans.factory.annotation.Value;
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
    private final HLSService hlsService;
    private final SongCustomRepository songCustomRepository;  // 검색 로직 위임
    private final SongPersistencePort songPersistencePort;

    @Value("${app.hls.directory}")
    private String hlsBasePath;


    /**
     * ID로 곡 조회
     */
    public SongResponse getSongById(String id) {
        Song song = songPersistencePort.findById(id)
                .orElseThrow(()-> SongNotFoundException.EXCEPTION);
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
        List<Song> songs = Mapper.toList(songPersistencePort.findAll());
        return SongListResponse.newInstanceBySongs(songs);
    }

    /**
     * 기존 스트리밍 방식 (제목으로 검색)
     */
    public ResponseEntity<Resource> streamSong(String title) throws IOException {
        Song song = songPersistencePort.findByTitle(title)
                .orElseThrow(() -> SongNotFoundException.EXCEPTION);

        // HLS 플레이리스트 파일로 리다이렉트
        return streamHLS(song.getId());
    }

    /**
     * HLS 스트리밍 (플레이리스트 파일 제공)
     */
    public ResponseEntity<Resource> streamHLS(String songId) throws IOException {
        Song song = songPersistencePort.findById(songId)
                .orElseThrow(() -> SongNotFoundException.EXCEPTION);

        // HLS 플레이리스트 파일 경로 구성
        Path playlistPath = Paths.get(hlsBasePath, songId, "playlist.m3u8");

        if (!Files.exists(playlistPath)) {
            throw new IllegalArgumentException("HLS 플레이리스트 파일을 찾을 수 없습니다: " + songId);
        }

        Resource resource = new UrlResource(playlistPath.toUri());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/vnd.apple.mpegurl");
        headers.add(HttpHeaders.CACHE_CONTROL, "max-age=3600");

        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }

    /**
     * HLS 세그먼트 파일 제공
     */
    public ResponseEntity<Resource> getHLSSegment(String songId, String segmentName) throws IOException {
        // 곡 존재 확인
        Song song = songPersistencePort.findById(songId)
                .orElseThrow(() -> SongNotFoundException.EXCEPTION);

        // 보안을 위해 파일명 검증
        if (!segmentName.matches("segment\\d+\\.ts")) {
            throw new IllegalArgumentException("유효하지 않은 세그먼트 파일명: " + segmentName);
        }

        // HLS 세그먼트 파일 경로 구성
        Path segmentPath = Paths.get(hlsBasePath, songId, segmentName);

        if (!Files.exists(segmentPath)) {
            throw new IllegalArgumentException("HLS 세그먼트 파일을 찾을 수 없습니다: " + segmentName);
        }

        Resource resource = new UrlResource(segmentPath.toUri());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "video/mp2t");
        headers.add(HttpHeaders.CACHE_CONTROL, "max-age=86400"); // 세그먼트는 더 긴 캐시

        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }

    /**
     * ID로 곡 삭제
     */
    public void deleteSong(String id) {
        // 곡 존재 확인
        songPersistencePort.findById(id)
                .orElseThrow(() -> SongNotFoundException.EXCEPTION);

        // DB에서 삭제
        songPersistencePort.deleteById(id);
        log.info("노래 삭제 완료: ID={}", id);
    }
}