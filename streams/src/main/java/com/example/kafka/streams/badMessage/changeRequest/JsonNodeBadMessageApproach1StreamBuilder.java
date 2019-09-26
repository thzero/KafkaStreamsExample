package com.example.kafka.streams.badMessage.changeRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.example.kafka.config.IKafkaConfigRetriever;
import com.example.kafka.streams.BaseStreamBuilder;
import com.example.kafka.topology.ITopology;

//@Component
public class JsonNodeBadMessageApproach1StreamBuilder extends BaseStreamBuilder {
    private final Logger logger = LoggerFactory.getLogger(JsonNodeBadMessageApproach1StreamBuilder.class);

    @Override
    protected IKafkaConfigRetriever getConfig() {
        return _configRetriever;
    }

    @Override
    protected ITopology getTopologyBuilder() {
        return _topologyBuilder;
    }

    @Autowired
    @Qualifier("jsonNodeBadMessageApproach1Config")
    private IKafkaConfigRetriever _configRetriever;
    @Autowired
    @Qualifier("jsonNodeBadMessageApproach1Topology")
    private ITopology _topologyBuilder;

}
