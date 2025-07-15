package org.example.remedy.global.config;

import lombok.RequiredArgsConstructor;
import org.example.remedy.domain.song.repository.SongElasticsearchRepository;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@RequiredArgsConstructor
@EnableElasticsearchRepositories(
        basePackages = "org.example.remedy.domain.song.repository",
        includeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = SongElasticsearchRepository.class  // 오직 이것만 포함
        )
)
public class ElasticsearchConfig {

    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo(elasticsearchProperties.getElasticsearchUrl())
                .build();
    }
}
