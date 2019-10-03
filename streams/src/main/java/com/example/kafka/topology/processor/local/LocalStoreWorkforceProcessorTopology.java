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
import com.example.kafka.service.merge.IMergeService;
import com.example.kafka.topology.WorkforceProcessorTopology;

@Component("localStoreWorkforceProcessorTopology")
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
                .addSource(KeySourceLoad, stringSerde.deserializer(), workforceSerde.deserializer(), appConfig.loadTopic)
                .addSource(KeySourceChangeRequestInput, stringSerde.deserializer(), workforceChangeRequestSerde.deserializer(), appConfig.changeRequestTopic)

                .addProcessor(LocalStoreWorkforceProcessor.TAG, () -> new LocalStoreWorkforceProcessor(KeyStore), KeySourceLoad)
                .addProcessor(LocalStoreWorkforceProcessorTopology.TAG, () -> new LocalStoreMergeProcessor(KeyStore, _mergeService), KeySourceChangeRequestInput)
                .addStateStore(workforceStoreBuilder, LocalStoreMergeProcessor.TAG, LocalStoreWorkforceProcessor.TAG)

                .addSink(LocalStoreMergeProcessor.KeySinkWorkforceCheckpoint, appConfig.changeRequestCheckpointTopic, stringSerde.serializer(), workforceChangeRequestSerde.serializer(), LocalStoreMergeProcessor.TAG)
                .addSink(LocalStoreMergeProcessor.KeySinkWorkforceDeadLetter, appConfig.changeRequestDeadLetterTopic, stringSerde.serializer(), workforceChangeRequestSerde.serializer(), LocalStoreMergeProcessor.TAG)
                .addSink(LocalStoreMergeProcessor.KeySinkWorkforceTransaction, appConfig.changeRequestTransactionTopic, stringSerde.serializer(), workforceChangeRequestSerde.serializer(), LocalStoreMergeProcessor.TAG)
                .addSink(LocalStoreMergeProcessor.KeySinkWorkforceTransactionInternal, appConfig.changeRequestTransactionInternalTopic, stringSerde.serializer(), workforceChangeRequestSerde.serializer(), LocalStoreMergeProcessor.TAG);
    }

    @Autowired
    private IMergeService _mergeService;

    public static final String KeySourceChangeRequestInput = "source-changeRequest-input";
    public static final String KeySourceLoad = "source-load";
    public static final String KeyStore = "local-store-workforce";

    private static final String TAG = LocalStoreWorkforceProcessorTopology.class.getName();
}
