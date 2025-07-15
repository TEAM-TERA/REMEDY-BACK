package org.example.remedy.global.config;

import org.example.remedy.domain.song.repository.SongElasticsearchRepository;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(
        basePackages = "org.example.remedy.domain",  // 전체 도메인 스캔
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = SongElasticsearchRepository.class  // Elasticsearch Repository 제외
        )
)
public class MongoConfig extends AbstractMongoClientConfiguration {
    @Override
    protected String getDatabaseName() {
        return "remedy";
    }

    @Override
    protected boolean autoIndexCreation() {
        return true;
    }
}