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
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
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
    private final SongCustomRepository songCustomRepository;  // 검색 로직 위임
    private final SongPersistencePort songPersistencePort;


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
    public ResponseEntity<Resource> streamSong(String title) {
        Song song = songPersistencePort.findByTitle(title)
                .orElseThrow(() -> SongNotFoundException.EXCEPTION);

        // HLS 플레이리스트 파일로 리다이렉트
        return streamHLS(song.getId());
    }

    /**
     * HLS 스트리밍 (S3 플레이리스트 파일로 리다이렉트)
     */
    public ResponseEntity<Resource> streamHLS(String songId) {
        Song song = songPersistencePort.findById(songId)
                .orElseThrow(() -> SongNotFoundException.EXCEPTION);

        // S3에 저장된 HLS 플레이리스트 URL로 리다이렉트
        if (song.getHlsPath() == null || song.getHlsPath().isEmpty()) {
            throw new IllegalArgumentException("HLS 플레이리스트 파일을 찾을 수 없습니다: " + songId);
        }

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(song.getHlsPath()))
                .build();
    }

    /**
     * HLS 세그먼트 파일 제공 (S3 세그먼트 파일로 리다이렉트)
     */
    public ResponseEntity<Resource> getHLSSegment(String songId, String segmentName) {
        // 곡 존재 확인
        Song song = songPersistencePort.findById(songId)
                .orElseThrow(() -> SongNotFoundException.EXCEPTION);

        // 보안을 위해 파일명 검증
        if (!segmentName.matches("segment\\d+\\.ts")) {
            throw new IllegalArgumentException("유효하지 않은 세그먼트 파일명: " + segmentName);
        }

        // S3 HLS 세그먼트 URL 생성
        String hlsPath = song.getHlsPath();
        if (hlsPath == null || hlsPath.isEmpty()) {
            throw new IllegalArgumentException("HLS 경로를 찾을 수 없습니다: " + songId);
        }

        String segmentUrl = hlsPath.substring(0, hlsPath.lastIndexOf("/")) + "/" + segmentName;

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(segmentUrl))
                .build();
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