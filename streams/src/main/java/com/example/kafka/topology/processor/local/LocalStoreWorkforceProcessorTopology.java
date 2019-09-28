package com.example.kafka.topology.processor.local;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.example.kafka.data.WorkforceData;
import com.example.kafka.service.IMergeService;
import com.example.kafka.topology.WorkforceProcessorTopology;
import com.example.kafka.topology.processor.global.GloablStoreMergeProcessor;

@Component("advancedProcessorWorkforceTopology")
public class LocalStoreWorkforceProcessorTopology extends WorkforceProcessorTopology {
    private static final Logger logger = LoggerFactory.getLogger(LocalStoreWorkforceProcessorTopology.class);

    @Override
    protected void defineTopology(@NonNull Topology builder) {
        Map<String, String> changelogConfig = new HashMap<>();
        // override min.insync.replicas
        changelogConfig.put("min.insync.replicas", "1");

        StoreBuilder<KeyValueStore<String, WorkforceData>> workforceStoreBuilder = Stores.keyValueStoreBuilder(
                Stores.persistentKeyValueStore(KeyStore),
                stringSerde,
                workforceSerde)
                .withLoggingEnabled(changelogConfig);

        builder
                .addSource(KeySourceChangeRequestInput, stringSerde.deserializer(), workforceChangeRequestSerde.deserializer(), appConfig.changeRequestTopic)

                .addProcessor(GloablStoreMergeProcessor.TAG, () -> new GloablStoreMergeProcessor(KeyStore, _mergeService), KeySourceChangeRequestInput)
                .addStateStore(workforceStoreBuilder, LocalStoreMergeProcessor.TAG)

                .addSink(GloablStoreMergeProcessor.KeySinkWorkforceDeadLetter, appConfig.changeRequestDeadLetterTopic, stringSerde.serializer(), workforceChangeRequestSerde.serializer(), GloablStoreMergeProcessor.TAG)
                .addSink(GloablStoreMergeProcessor.KeySinkWorkforce, appConfig.changeRequestOutputTopic, stringSerde.serializer(), workforceChangeRequestSerde.serializer(), GloablStoreMergeProcessor.TAG)
                .addSink(GloablStoreMergeProcessor.KeySinkWorkforceTransaction, appConfig.changeRequestTransactionTopic, stringSerde.serializer(), workforceChangeRequestSerde.serializer(), GloablStoreMergeProcessor.TAG);
    }

    @Autowired
    private IMergeService _mergeService;

    public static final String KeySourceChangeRequestInput = "changeRequest-input";
    public static final String KeyStore = "workforce";
    public static final String KeySourceLoad = "workforce-load";

    private static final String TAG = LocalStoreWorkforceProcessorTopology.class.getName();
}
