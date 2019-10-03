package com.example.kafka.topology.processor.external;

import org.apache.kafka.streams.Topology;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.example.kafka.service.merge.IMergeService;
import com.example.kafka.service.store.IStoreWorkforceService;
import com.example.kafka.topology.WorkforceProcessorTopology;

@Component("externalWorkforceProcessorTopology")
public class ExternalWorkforceProcessorTopology extends WorkforceProcessorTopology {
    private static final Logger logger = LoggerFactory.getLogger(ExternalWorkforceProcessorTopology.class);

    @Override
    protected void defineTopology(@NonNull Topology builder) {
        builder
                .addSource(KeySourceChangeRequestInput, stringSerde.deserializer(), workforceChangeRequestSerde.deserializer(), appConfig.changeRequestTopic)

                .addProcessor(ExternalMergeProcessor.TAG, () -> new ExternalMergeProcessor(_mergeService, _storeService), KeySourceChangeRequestInput)

                .addSink(ExternalMergeProcessor.KeySinkWorkforceCheckpoint, appConfig.changeRequestCheckpointTopic, stringSerde.serializer(), workforceChangeRequestSerde.serializer(), ExternalMergeProcessor.TAG)
                .addSink(ExternalMergeProcessor.KeySinkWorkforceDeadLetter, appConfig.changeRequestDeadLetterTopic, stringSerde.serializer(), workforceChangeRequestSerde.serializer(), ExternalMergeProcessor.TAG)
                .addSink(ExternalMergeProcessor.KeySinkWorkforceTransaction, appConfig.changeRequestTransactionTopic, stringSerde.serializer(), workforceChangeRequestSerde.serializer(), ExternalMergeProcessor.TAG)
                .addSink(ExternalMergeProcessor.KeySinkWorkforceTransactionInternal, appConfig.changeRequestTransactionInternalTopic, stringSerde.serializer(), workforceChangeRequestSerde.serializer(), ExternalMergeProcessor.TAG);
    }

    @Autowired
    private IMergeService _mergeService;

    @Autowired
    private IStoreWorkforceService _storeService;

    public static final String KeySourceChangeRequestInput = "source-changeRequest-input";

    private static final String TAG = ExternalWorkforceProcessorTopology.class.getName();
}
