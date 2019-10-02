package com.example.kafka.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppConfig extends BaseConfig {
    @Value("${workforce.messages-per-request}")
    public int messagesPerRequest;
    public int waitDelay = 25;
}
