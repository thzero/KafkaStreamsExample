package com.example.kafka.streams.advanced;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.example.kafka.config.IKafkaConfigRetriever;
import com.example.kafka.streams.BaseStreamBuilder;
import com.example.kafka.topology.ITopology;

//@Component
public class ProcessorWorkforceStreamBuilder extends BaseStreamBuilder {
    private final Logger logger = LoggerFactory.getLogger(ProcessorWorkforceStreamBuilder.class);

    @Override
    protected IKafkaConfigRetriever getConfig() {
        return _configRetriever;
    }

    @Override
    protected ITopology getTopologyBuilder() {
        return _topologyBuilder;
    }

    @Autowired
    @Qualifier("advancedProcessorWorkforceConfig")
    private IKafkaConfigRetriever _configRetriever;
    @Autowired
    @Qualifier("advancedProcessorWorkforceTopology")
    private ITopology _topologyBuilder;

}
