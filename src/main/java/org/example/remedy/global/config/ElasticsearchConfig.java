package org.example.remedy.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;

@Configuration
@RequiredArgsConstructor
public class ElasticsearchConfig extends ElasticsearchConfiguration {
    @Value("${spring.elasticsearch.uris}")
    private String elasticsearchUri;

    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo(elasticsearchUri)
                .build();
    }
}
