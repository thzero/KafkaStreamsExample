package com.example.kafka.topology.simple;

import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.kafka.config.AppConfig;
import com.example.kafka.topology.ITopology;

@Component("simpleTopology")
public class SimpleTopology implements ITopology {
    private static final Logger logger = LoggerFactory.getLogger(SimpleTopology.class);

    @Override
    public Topology defineTopology(){
        StreamsBuilder builder = new StreamsBuilder();

        // Input stream from the input topic.
        KStream<String, String> inputStream = builder.stream(_appConfig.changeRequestTopic);

        // Setup output topic... this is where a Kafka Connect would pull from for loading into Mongo (or any other Kafka Connect datasource)
        inputStream.to(_appConfig.changeRequestOutputTopic);

        return builder.build();
    }

    @Autowired
    private AppConfig _appConfig;
}
