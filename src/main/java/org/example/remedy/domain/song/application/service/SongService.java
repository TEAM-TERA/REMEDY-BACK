package org.example.remedy.domain.song.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.remedy.domain.song.application.dto.response.SongListResponse;
import org.example.remedy.domain.song.application.dto.response.SongResponse;
import org.example.remedy.domain.song.application.dto.response.SongSearchListResponse;
import org.example.remedy.domain.song.application.exception.SongNotFoundException;
import org.example.remedy.domain.song.repository.SongRepository;
import org.example.remedy.domain.song.domain.Song;
import org.example.remedy.global.util.mapper.Mapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 간소화된 노래 서비스
 * 검색 로직은 SongQueryBuilder로 분리
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SongService {
    private final SongRepository songRepository;


    /**
     * ID로 곡 조회
     */
    public SongResponse getSongById(String id) {
        Song song = songRepository.findById(id)
                .orElseThrow(()-> SongNotFoundException.EXCEPTION);
        return SongResponse.newInstance(song);
    }

    /**
     * 유사도 기반 통합 검색 (SongQueryBuilder 위임)
     */
    public SongSearchListResponse searchSongs(String query) {
        List<Song> songs = songRepository.searchSongs(query);
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

    /**
     * ID로 곡 삭제
     */
    public void deleteSong(String id) {
        // 곡 존재 확인
        songRepository.findById(id)
                .orElseThrow(() -> SongNotFoundException.EXCEPTION);

        // DB에서 삭제
        songRepository.deleteById(id);
        log.info("노래 삭제 완료: ID={}", id);
    }
}