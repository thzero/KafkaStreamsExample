package com.example.kafka.streams.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.example.kafka.config.IKafkaConfigRetriever;
import com.example.kafka.streams.BaseStreamBuilder;
import com.example.kafka.topology.ITopology;

//@Component
public class GlobalStoreWorkforceProcessorStreamBuilder extends BaseStreamBuilder {
    @Override
    protected IKafkaConfigRetriever getConfig() {
        return _configRetriever;
    }

    @Override
    protected ITopology getTopologyBuilder() {
        return _topologyBuilder;
    }

    @Autowired
    @Qualifier("globalStoreWorkforceProcessorConfig")
    private IKafkaConfigRetriever _configRetriever;
    @Autowired
    @Qualifier("globalStoreWorkforceProcessorTopology")
    private ITopology _topologyBuilder;

}
