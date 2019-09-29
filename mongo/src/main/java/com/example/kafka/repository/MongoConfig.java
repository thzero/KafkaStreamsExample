package com.example.kafka.repository;

import com.mongodb.MongoClient;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoConfig {
    @Bean
    public MongoClient mongo() {
        return new MongoClient();
    }

    @Bean
    @Qualifier("workforce")
    public MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(mongo(), "workforce");
    }
}