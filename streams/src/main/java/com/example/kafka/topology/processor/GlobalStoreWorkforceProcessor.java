package com.example.kafka.topology.processor;

import java.util.Objects;

import org.apache.kafka.streams.processor.AbstractProcessor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.kafka.data.WorkforceData;

public class GlobalStoreWorkforceProcessor extends AbstractProcessor<String, WorkforceData> {
    private static final Logger logger = LoggerFactory.getLogger(GlobalStoreWorkforceProcessor.class);

    public GlobalStoreWorkforceProcessor() {}
    public GlobalStoreWorkforceProcessor(String storeName) {
        _storeName = storeName;
    }

    @Override
    public void process(String key, WorkforceData workforce) {
        logger.debug("joinedStream - load for request id '{}'", workforce.id);
        _workforceStore.put(workforce.id, workforce);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void init(ProcessorContext context) {
        _workforceStore = (KeyValueStore<String, WorkforceData>)context.getStateStore(_storeName);
        Objects.requireNonNull(_workforceStore, "State store can't be null");
    }

    private String _storeName;
    private KeyValueStore<String, WorkforceData> _workforceStore;

    public static final String TAG = GlobalStoreWorkforceProcessor.class.getName();
}
