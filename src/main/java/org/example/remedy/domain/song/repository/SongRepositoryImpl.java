package org.example.remedy.domain.song.repository;

import co.elastic.clients.elasticsearch._types.query_dsl.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.remedy.domain.song.domain.Song;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class SongRepositoryImpl implements SongRepository {
    private final SongPersistenceRepository songPersistenceRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public Optional<Song> findById(String id) {
        return songPersistenceRepository.findById(id);
    }

    @Override
    public Iterable<Song> findAll() {
        return songPersistenceRepository.findAll();
    }

    @Override
    public Song save(Song song) {
        return songPersistenceRepository.save(song);
    }

    @Override
    public void deleteById(String id) {
        songPersistenceRepository.deleteById(id);
    }

    /**
     * 통합 유사도 검색 메인 메서드
     * 지원 패턴:
     * - "노래제목"
     * - "가수이름"
     * - "노래제목 가수이름"
     * - "가수이름 노래제목"
     */
    @Override
    public List<Song> searchSongs(String query) {
        if (query == null || query.trim().isEmpty()) {
            return List.of();
        }

        String cleanQuery = query.trim();
        String[] keywords = cleanQuery.split("\\s+");

        try {
            List<Song> results = performElasticsearchSearch(cleanQuery, keywords);
            if (!results.isEmpty()) {
                return results;
            }
        } catch (Exception e) {
            log.warn("Elasticsearch 검색 실패, fallback 사용: {}", e.getMessage());
        }

        return songPersistenceRepository
                .findByTitleContainingOrArtistContaining(cleanQuery, cleanQuery); // 여기도 없으면 List.of() 반환됨
    }

    /**
     * Elasticsearch 유사도 검색 로직
     */
    private List<Song> performElasticsearchSearch(String query, String[] keywords) {
        Query searchQuery = switch (keywords.length) {
            case 1 -> buildSingleKeywordQuery(keywords[0]);
            case 2 -> buildTwoKeywordsQuery(keywords[0], keywords[1]);
            default -> buildMultiKeywordQuery(query);
        };

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(searchQuery)
                .withMaxResults(20)
                .build();

        SearchHits<Song> searchHits = elasticsearchOperations.search(nativeQuery, Song.class);
        return searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

    /**
     * 단일 키워드 검색 (정확도 + 유사도 + 부분일치)
     */
    private Query buildSingleKeywordQuery(String keyword) {
        return BoolQuery.of(b -> b
                .should(s -> s.match(m -> m.field("title").query(keyword).boost(3.0f)))
                .should(s -> s.match(m -> m.field("artist").query(keyword).boost(3.0f)))
                .should(s -> s.fuzzy(f -> f.field("title").value(keyword).fuzziness("AUTO").boost(2.0f)))
                .should(s -> s.fuzzy(f -> f.field("artist").value(keyword).fuzziness("AUTO").boost(2.0f)))
                .should(s -> s.wildcard(w -> w.field("title").value("*" + keyword + "*").boost(1.5f)))
                .should(s -> s.wildcard(w -> w.field("artist").value("*" + keyword + "*").boost(1.5f)))
                .minimumShouldMatch("1")
        )._toQuery();
    }

    /**
     * 두 키워드 검색 ("제목 가수" 또는 "가수 제목")
     */
    private Query buildTwoKeywordsQuery(String keyword1, String keyword2) {
        return BoolQuery.of(b -> b
                // 첫번째=제목, 두번째=가수
                .should(s -> s.bool(bool -> bool
                        .must(m -> m.match(match -> match.field("title").query(keyword1).boost(3.0f)))
                        .must(m -> m.match(match -> match.field("artist").query(keyword2).boost(3.0f)))
                ))
                // 첫번째=가수, 두번째=제목
                .should(s -> s.bool(bool -> bool
                        .must(m -> m.match(match -> match.field("artist").query(keyword1).boost(3.0f)))
                        .must(m -> m.match(match -> match.field("title").query(keyword2).boost(3.0f)))
                ))
                // 두 키워드 모두 제목에서
                .should(s -> s.bool(bool -> bool
                        .must(m -> m.match(match -> match.field("title").query(keyword1).boost(2.0f)))
                        .must(m -> m.match(match -> match.field("title").query(keyword2).boost(2.0f)))
                ))
                // 두 키워드 모두 가수에서
                .should(s -> s.bool(bool -> bool
                        .must(m -> m.match(match -> match.field("artist").query(keyword1).boost(2.0f)))
                        .must(m -> m.match(match -> match.field("artist").query(keyword2).boost(2.0f)))
                ))
                // 퍼지 매치
                .should(s -> s.fuzzy(f -> f.field("title").value(keyword1 + " " + keyword2).fuzziness("AUTO").boost(1.5f)))
                .should(s -> s.fuzzy(f -> f.field("artist").value(keyword1 + " " + keyword2).fuzziness("AUTO").boost(1.5f)))
                .minimumShouldMatch("1")
        )._toQuery();
    }

    /**
     * 다중 키워드 검색 (3개 이상)
     */
    private Query buildMultiKeywordQuery(String query) {
        return MultiMatchQuery.of(m -> m
                .query(query)
                .fields("title^2", "artist^2")
                .type(co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType.BestFields)
                .fuzziness("AUTO")
                .minimumShouldMatch("70%")
        )._toQuery();
    }
}