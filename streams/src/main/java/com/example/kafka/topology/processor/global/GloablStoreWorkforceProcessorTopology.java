package com.example.kafka.topology.processor.global;

import java.util.HashMap;
import java.util.Map;

import com.example.kafka.data.WorkforceData;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.example.kafka.service.IMergeService;
import com.example.kafka.topology.WorkforceProcessorTopology;

@Component("advancedProcessorWorkforceTopology")
public class GloablStoreWorkforceProcessorTopology extends WorkforceProcessorTopology {
    private static final Logger logger = LoggerFactory.getLogger(GloablStoreWorkforceProcessorTopology.class);

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

                .addProcessor(GloablStoreMergeProcessor.TAG, () -> new GloablStoreMergeProcessor(KeyStore, _mergeService), KeySourceChangeRequestInput)

                .addSink(GloablStoreMergeProcessor.KeySinkWorkforceDeadLetter, appConfig.changeRequestDeadLetterTopic, stringSerde.serializer(), workforceChangeRequestSerde.serializer(), GloablStoreMergeProcessor.TAG)
                .addSink(GloablStoreMergeProcessor.KeySinkWorkforce, appConfig.changeRequestOutputTopic, stringSerde.serializer(), workforceChangeRequestSerde.serializer(), GloablStoreMergeProcessor.TAG)
                .addSink(GloablStoreMergeProcessor.KeySinkWorkforceTransaction, appConfig.changeRequestTransactionTopic, stringSerde.serializer(), workforceChangeRequestSerde.serializer(), GloablStoreMergeProcessor.TAG);
    }

    @Autowired
    private IMergeService _mergeService;

    public static final String KeySourceChangeRequestInput = "changeRequest-input";
    public static final String KeyStore = "workforce";
    public static final String KeySourceLoad = "workforce-load";

    private static final String TAG = GloablStoreWorkforceProcessorTopology.class.getName();
}
