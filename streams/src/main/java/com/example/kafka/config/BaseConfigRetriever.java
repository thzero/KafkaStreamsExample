package com.example.kafka.config;

import java.util.Properties;

import org.apache.kafka.streams.StreamsConfig;

import org.springframework.beans.factory.annotation.Value;

public class BaseConfigRetriever implements IKafkaConfigRetriever {
    public Properties getProps() {
        Properties props = new Properties();

        // App
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, getAppId());
        props.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, _maxNumThreads);

        // Kafka
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, _kafkaBroker);

        return props;
    }

    protected String getAppId() {
        return null;
    }

    @Value("${spring.kafka.bootstrap-servers}")
    private String _kafkaBroker;

    @Value("${spring.kafka.num.stream.threads}")
    private int _maxNumThreads;
}