package com.example.kafka.streams.badMessage.changeRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.example.kafka.config.IKafkaConfigRetriever;
import com.example.kafka.streams.BaseStreamBuilder;
import com.example.kafka.topology.ITopology;

//@Component
public class WorkforceBadMessageApproach2StreamBuilder extends BaseStreamBuilder {
    private final Logger logger = LoggerFactory.getLogger(WorkforceBadMessageApproach2StreamBuilder.class);

    @Override
    protected IKafkaConfigRetriever getConfig() {
        return _configRetriever;
    }

    @Override
    protected ITopology getTopologyBuilder() {
        return _topologyBuilder;
    }

    @Autowired
    @Qualifier("workforceBadMessageApproach2Config")
    private IKafkaConfigRetriever _configRetriever;
    @Autowired
    @Qualifier("workforceBadMessageApproach2Topology")
    private ITopology _topologyBuilder;

}
