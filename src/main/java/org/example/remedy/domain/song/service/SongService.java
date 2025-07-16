package org.example.remedy.domain.song.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.remedy.domain.song.domain.Song;
import org.example.remedy.domain.song.dto.YouTubeMetadata;
import org.example.remedy.domain.song.dto.request.SongCreateRequest;
import org.example.remedy.domain.song.dto.response.SongListResponse;
import org.example.remedy.domain.song.dto.response.SongResponse;
import org.example.remedy.domain.song.dto.response.SongSearchListResponse;
import org.example.remedy.domain.song.exception.SongNotFoundException;
import org.example.remedy.domain.song.mapper.Mapper;
import org.example.remedy.domain.song.repository.SongRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class SongService {
    private final YouTubeService youTubeService;
    private final HLSService hlsService;
    private final SongQueryBuilder songQueryBuilder;  // 검색 로직 위임
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
        List<Song> songs = songQueryBuilder.searchSongs(query);
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
}