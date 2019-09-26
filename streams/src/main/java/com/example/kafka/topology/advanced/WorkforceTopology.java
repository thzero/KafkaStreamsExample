package com.example.kafka.topology.advanced;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.example.kafka.data.SplitTypes;
import com.example.kafka.data.WorkforceChangeRequestData;
import com.example.kafka.data.WorkforceData;
import com.example.kafka.response.ISuccessResponse;
import com.example.kafka.service.IMergeService;
import com.example.kafka.topology.WorkforceBaseTopology;

@Component("advancedWorkforceTopology")
public class WorkforceTopology extends WorkforceBaseTopology {
    private static final Logger logger = LoggerFactory.getLogger(WorkforceTopology.class);

    @Override
    protected void defineTopology(@NonNull StreamsBuilder builder) {
        // Setup GlobalKTable.
        // Maybe should use KTable instead; KTables are partitioned by key, which is the same key we use for the rest of the
        // stream, the KTable should have the correct information for the lookup.
        GlobalKTable<String, WorkforceData> workforcesTable = builder.globalTable(appConfig.loadTopic, Materialized.<String, WorkforceData, KeyValueStore<Bytes, byte[]>>as(appConfig.workforceTable)
                .withKeySerde(stringSerde)
                .withValueSerde(workforceSerde));

        // Input stream from the input topic.
        // Serializing to WorkforceChangeRequestData as all incoming are guaranteed to be in that format as the producer is an api that
        // validates all incoming requests.
        KStream<String, WorkforceChangeRequestData> inputStream = builder.stream(appConfig.changeRequestTopic, Consumed.with(stringSerde, workforceChangeRequestSerde));

//        https://www.javatips.net/api/examples-master/kafka-streams/src/main/java/io/confluent/examples/streams/GlobalKTablesExample.java
//        https://www.confluent.fr/blog/crossing-streams-joins-apache-kafka/
        // Join the incoming change request serializedStream with the GlobalKTable that holds the current WorkforceData documents.
        // The key for the serialized stream is the change request key, the key for the GlobalKTable is the workforceData document id.
        // The KeyValueMapper will map the join to the change request's workforceData (initial) document id.
        // This step will combine the change request to the joined full document.
        //
        // The GlobalKTable has to be updated independently... there might be some lag, would it be better to wait for external
        // connect to fetch the document, the write the document, before moving on?
        //
        // Or use a producer api?
        KStream<String, WorkforceChangeRequestData> joinedStream =
                inputStream.leftJoin(workforcesTable,
                (key, value) -> {
                    logger.debug("joinedStream - received key '{}'", key);
                    logger.debug("joinedStream - received value id '{}'", value.id);
                    logger.debug("joinedStream - received value request id '{}'", value.getRequestId());
                    return value.getRequestId();
                },
                (leftValue, rightValue) -> {
                    logger.debug("joinedStream - joiner for request id '{}'", leftValue.getRequestId());
                    if (rightValue != null)
                        logger.debug("joinedStream - workforce data for request id '{}' was found!", leftValue.getRequestId());
                    else
                        logger.warn("joinedStream - workforce data for request id '{}' was not found!", leftValue.getRequestId());

                    logger.debug("joinedStream - before, key: '{}' | leftValue: {} | rightValue: {}", leftValue.getRequestId(), leftValue.toString(), (rightValue != null ? rightValue.toString() : null));
                    ISuccessResponse response = _mergeService.merge(leftValue, rightValue);
                    if (!response.isSuccess()) {
                        // TODO: Handle error?
                        logger.warn("joinedStream - workforce data for request id '{}' had the following error: {}", leftValue.getRequestId(), response.getError());
                        return leftValue;
                    }
                    logger.debug("joinedStream - after, key: '{}' | leftValue: {}", leftValue.getRequestId(), leftValue.toString());

                    return leftValue;
                });

        // Splitting the stream - send one record to the output topic, and another to the the transaction topic.
        // There is no native kafka copy or duplicate function, so using the flatMap to create a duplicate transaction record with a specific SplitType.Transaction.
        // Then perform a branch on the results, the branch will send the SplitType.Transactions to one partition and anything else to another partition.
        // Another approach would be the write the output of joinedStream to a topic.  Then have two separate streams that read from the topic, one for purposes
        // of transaction and another to pull the workforce data item.
        KStream<String, WorkforceChangeRequestData> splitStream =
            joinedStream.flatMap((KeyValueMapper<String, WorkforceChangeRequestData, Iterable<? extends KeyValue<String, WorkforceChangeRequestData>>>) (key, value) -> {
                List<KeyValue<String, WorkforceChangeRequestData>> result = new ArrayList<>();
                logger.debug("splitStream - key: '{}' | value: {}", value.getRequestId(), value.toString());
                WorkforceChangeRequestData changeRequest = new WorkforceChangeRequestData(UUID.randomUUID().toString(), value, SplitTypes.Transaction);
                result.add(new KeyValue<>(changeRequest.id, changeRequest));
                result.add(new KeyValue<>(key, value));
                return result;
            });

        // Partitioned[0] contains the entity to be sent to the transaction topic
        // Partitioned[1] contains the entity to be sent to the output topic
        // Partitioned[2] contains dead letter entities that did not have a snapshot result for some reason
        KStream<String, WorkforceChangeRequestData>[] partitionedOutputStream = splitStream.branch(
                (k, v) -> {
                    return (v.splitType == SplitTypes.Transaction);
                },
                (k, v) -> {
                    return (v.snapshot != null);
                },
                (k, v) -> true
        );

        // Write the transaction data to the transaction topic.
        partitionedOutputStream[0].to(appConfig.changeRequestTransactionTopic, Produced.with(stringSerde, workforceChangeRequestSerde));

        // Bad messages - Issue where they did not get a snapshot created.
        partitionedOutputStream[2].to(appConfig.changeRequestDeadLetterTopic, Produced.with(stringSerde, workforceChangeRequestSerde));

        // Map down to the workforce data only and provide that to the output topic.
        KStream<String, WorkforceData> outputStream =
            partitionedOutputStream[1].map((KeyValueMapper<String, WorkforceChangeRequestData, KeyValue<String, WorkforceData>>) (key, value) -> {
                logger.debug("outputStream - key: '{}' | value: {}", value.snapshot.id, value.snapshot.toString());
                KeyValue<String, WorkforceData> pair = new KeyValue<>(value.snapshot.id, value.snapshot);
                return pair;
            });

        // Write the workforce data output to the output topic
        outputStream.to(appConfig.changeRequestOutputTopic, Produced.with(stringSerde, workforceSerde));
    }

    @Autowired
    private IMergeService _mergeService;

    private static final String TAG = WorkforceTopology.class.getName();
}
