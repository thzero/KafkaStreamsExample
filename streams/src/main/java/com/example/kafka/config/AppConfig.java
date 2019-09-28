package com.example.kafka.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppConfig extends BaseConfig {
    @Value("${workforce.tables.workforce.name}")
    public String workforceTable;
}
