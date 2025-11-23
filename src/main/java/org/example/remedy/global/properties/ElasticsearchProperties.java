package org.example.remedy.global.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties("elasticsearch")
public class ElasticsearchProperties {
    private final String elasticsearchUrl;
}
