package org.example.remedy.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

@Configuration
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