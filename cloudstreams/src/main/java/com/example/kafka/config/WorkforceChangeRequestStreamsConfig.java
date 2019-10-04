package com.example.kafka.config;

import org.springframework.cloud.stream.annotation.EnableBinding;

import com.example.kafka.listener.WorkforceChangeRequestStreams;

@EnableBinding(WorkforceChangeRequestStreams.class)
public class WorkforceChangeRequestStreamsConfig {
}
