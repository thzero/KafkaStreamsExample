package com.example.kafka.config;

import org.springframework.beans.factory.annotation.Value;

public abstract class BaseConfig {
    @Value("${workforce.topics.change-request.name}")
    public String changeRequestTopic;
    @Value("${workforce.topics.change-request-checkpoint.name}")
    public String changeRequestCheckpointTopic;
    @Value("${workforce.topics.change-request-dead-letter.name}")
    public String changeRequestDeadLetterTopic;
    @Value("${workforce.topics.change-request-output.name}")
    public String changeRequestOutputTopic;
    @Value("${workforce.topics.change-request-transaction.name}")
    public String changeRequestTransactionTopic;
    @Value("${workforce.topics.change-request-transaction-internal.name}")
    public String changeRequestTransactionInternalTopic;
    @Value("${workforce.topics.dead-letter.name}")
    public String deadLetterTopic;
    @Value("${workforce.topics.load.name}")
    public String loadTopic;
    @Value("${workforce.topics.load-output.name}")
    public String loadOutputTopic;
    @Value("${workforce.topics.output.name}")
    public String outputTopic;
}

