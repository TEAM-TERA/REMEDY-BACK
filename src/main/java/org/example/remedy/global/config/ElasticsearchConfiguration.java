package org.example.remedy.global.config;

import lombok.RequiredArgsConstructor;
import org.example.remedy.domain.song.repository.SongElasticsearchRepository;
import org.example.remedy.global.config.properties.ElasticsearchProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@RequiredArgsConstructor
@EnableElasticsearchRepositories(basePackageClasses = SongElasticsearchRepository.class)
public class ElasticsearchConfiguration extends org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration {
    private final ElasticsearchProperties elasticsearchProperties;

    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo(elasticsearchProperties.getElasticsearchUrl())
                .build();
    }
}
