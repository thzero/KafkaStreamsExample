package com.example.kafka.topology.processor.global;

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

@Component("globalStoreWorkforceProcessorTopology")
public class GlobalStoreWorkforceProcessorTopology extends WorkforceProcessorTopology {
    private static final Logger logger = LoggerFactory.getLogger(GlobalStoreWorkforceProcessorTopology.class);

    @Override
    protected void defineTopology(@NonNull Topology builder) {
        StoreBuilder<KeyValueStore<String, WorkforceData>> workforceStoreBuilder = Stores.keyValueStoreBuilder(
                Stores.persistentKeyValueStore(KeyStore),
                stringSerde,
                workforceSerde)
                .withLoggingDisabled();

        builder
                .addGlobalStore(workforceStoreBuilder, KeySourceLoad, stringSerde.deserializer(), workforceSerde.deserializer(), appConfig.loadTopic, GlobalStoreWorkforceProcessor.TAG, () -> new GlobalStoreWorkforceProcessor(KeyStore))
                .addSource(KeySourceChangeRequestInput, stringSerde.deserializer(), workforceChangeRequestSerde.deserializer(), appConfig.changeRequestTopic)

                .addProcessor(GlobalStoreMergeProcessor.TAG, () -> new GlobalStoreMergeProcessor(KeyStore, _mergeService), KeySourceChangeRequestInput)

                .addSink(GlobalStoreMergeProcessor.KeySinkWorkforceCheckpoint, appConfig.changeRequestCheckpointTopic, stringSerde.serializer(), workforceChangeRequestSerde.serializer(), GlobalStoreMergeProcessor.TAG)
                .addSink(GlobalStoreMergeProcessor.KeySinkWorkforceDeadLetter, appConfig.changeRequestDeadLetterTopic, stringSerde.serializer(), workforceChangeRequestSerde.serializer(), GlobalStoreMergeProcessor.TAG)
                .addSink(GlobalStoreMergeProcessor.KeySinkWorkforceTransaction, appConfig.changeRequestTransactionTopic, stringSerde.serializer(), workforceChangeRequestSerde.serializer(), GlobalStoreMergeProcessor.TAG)
                .addSink(GlobalStoreMergeProcessor.KeySinkWorkforceTransactionInternal, appConfig.changeRequestTransactionInternalTopic, stringSerde.serializer(), workforceChangeRequestSerde.serializer(), GlobalStoreMergeProcessor.TAG)
                .addSink(GlobalStoreMergeProcessor.KeySinkWorkforceLoad, appConfig.loadTopic, stringSerde.serializer(), workforceSerde.serializer(), GlobalStoreMergeProcessor.TAG);
    }

    @Autowired
    private IMergeService _mergeService;

    public static final String KeySourceChangeRequestInput = "source-changeRequest-input";
    public static final String KeySourceLoad = "source-workforce-load";
    public static final String KeyStore = "global-store-workforce";

    private static final String TAG = GlobalStoreWorkforceProcessorTopology.class.getName();
}
