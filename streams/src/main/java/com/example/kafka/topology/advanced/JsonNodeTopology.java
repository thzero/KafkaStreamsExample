///*
//package com.example.kafka.topology.advanced;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//
//import com.fasterxml.jackson.databind.JsonNode;
//
//import org.apache.kafka.common.errors.SerializationException;
//import org.apache.kafka.common.utils.Bytes;
//import org.apache.kafka.streams.KeyValue;
//import org.apache.kafka.streams.StreamsBuilder;
//import org.apache.kafka.streams.kstream.*;
//import org.apache.kafka.streams.state.KeyValueStore;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.lang.NonNull;
//import org.springframework.stereotype.Component;
//
//import com.example.kafka.data.SplitTypes;
//import com.example.kafka.data.WorkforceChangeRequestData;
//import com.example.kafka.data.WorkforceData;
//import com.example.kafka.service.IMergeService;
//import com.example.kafka.topology.JsonNodeBaseTopology;
//
//@Component("advancedWorkforceTopology")
//public class JsonNodeTopology extends JsonNodeBaseTopology {
//    private static final Logger logger = LoggerFactory.getLogger(JsonNodeTopology.class);
//
//    @Override
//    protected void defineTopology(@NonNull StreamsBuilder builder) {
//        // Input stream from the input topic.
//        KStream<String, String> inputStream = builder.stream(appConfig.changeRequestTopic);
//
//        GlobalKTable<String, JsonNode> workforcesTable = builder.globalTable(appConfig.loadTopic, Materialized.<String, JsonNode, KeyValueStore<Bytes, byte[]>>as(appConfig.workforceTable)
//                .withKeySerde(stringSerde)
//                .withValueSerde(jsonNodeSerde));
//
//        // Read input stream... using option 2
//        // This branches on a unserializable input
//        // https://stackoverflow.com/questions/42666756/handling-bad-messages-using-kafkas-streams-api
//        // Partitioned[0] is the KStream<String, String> that contains only valid records.
//        // Partitioned[1] contains only corrupted records and thus acts as a "dead letter queue".
//        KStream<String, String>[] partitionedStream = inputStream.branch(
//            (k, v) -> {
//                try {
//                    jsonNodeSerde.deserializer().deserialize(appConfig.changeRequestTopic, v.getBytes());
//                    return true;
//                }
//                catch (SerializationException ignored) {
//                }
//                return false;
//            },
//            (k, v) -> true
//        );
//
//        // Serialize the value as an object.
//        final KStream<String, JsonNode> serializedStream = partitionedStream[0].map(
//            (k, v) -> {
//                // Must deserialize a second time unfortunately. :(
//                JsonNode value = jsonNodeSerde.deserializer().deserialize(appConfig.changeRequestTopic, v.getBytes());
//
//                logger.debug("serializedStream received key '{}' | Payload: {} | Record: {}", k, v, value.toString());
//                return KeyValue.pair(k, value);
//            });
//
//        // Bad messages - Don't forget to actually write the dead letter queue back to Kafka!
//        partitionedStream[1].to(appConfig.deadLetterTopic, Produced.with(stringSerde, stringSerde));
//
////        https://www.javatips.net/api/examples-master/kafka-streams/src/main/java/io/confluent/examples/streams/GlobalKTablesExample.java
////        https://www.confluent.fr/blog/crossing-streams-joins-apache-kafka/
//        // Join the incoming change request serializedStream with the GlobalKTable that holds the current WorkforceData documents.
//        // The key for the serialized stream is the change request key, the key for the GlobalKTable is the workforceData document id.
//        // The KeyValueMapper will map the join to the change request's workforceData (initial) document id.
//        // This step will combine the change request to the joined full document.
//        KStream<String, JsonNode> joinedStream =
//            serializedStream.leftJoin(workforcesTable,
//                (key, value) -> {
//                    logger.debug("joinedStream received key '{}'", key);
//                    logger.debug("joinedStream received value id '{}'", value.id);
//                    logger.debug("joinedStream received value request id '{}'", value.getRequestId());
//                    return value.getRequestId();
//                },
//                (leftValue, rightValue) -> {
//                    logger.debug("joinedStream joiner for request id '{}'", leftValue.getRequestId());
//                    if (rightValue == null) {
//                        logger.warn("joinedStream workforce data for request id '{}' was not found!", leftValue.getRequestId());
//                        return leftValue;
//                    }
//
//                    logger.debug("joinedStream workforce data for request id '{}' was found!", leftValue.getRequestId());
//
//                    // TODO: Merge into rightValue
//                    leftValue.snapshot = rightValue;
//
//                    return leftValue;
//                });
//
//        // Splitting the stream - send one record to the output topic, and another to the the transaction topic.
//        // There is no native kafka copy or duplicate function, so using the flatMap to create a duplicate transaction record with a specific SplitType.Transaction.
//        // Then perform a branch on the results, the branch will send the SplitType.Transactions to one partition and anything else to another partition.
//        // Another approach would be the write the output of joinedStream to a topic.  Then have two separate streams that read from the topic, one for purposes
//        // of transaction and another to pull the workforce data item.
//        KStream<String, JsonNode> splitStream =
//            joinedStream.flatMap((KeyValueMapper<String, JsonNode, Iterable<? extends KeyValue<String, JsonNode>>>) (key, value) -> {
//                List<KeyValue<String, JsonNode>> result = new ArrayList<>();
//                WorkforceChangeRequestData changeRequest = new WorkforceChangeRequestData(UUID.randomUUID().toString(), value, SplitTypes.Transaction);
//                result.add(new KeyValue<>(changeRequest.id, changeRequest));
//                result.add(new KeyValue<>(key, value));
//                return result;
//            });
//
//        // Partitioned[0] is the KStream<String, WorkforceData> that contains the entity to be sent to the output topic
//        // Partitioned[1] contains the entity to be sent to the transaction topic
//        KStream<String, JsonNode>[] partitionedOutputStream = splitStream.branch(
//                (k, v) -> {
//                    return (v.splitType == SplitTypes.Transaction);
//                },
//                (k, v) -> true
//        );
//
//        // Write the transaction data to the transaction topic.
//        partitionedOutputStream[0].to(appConfig.changeRequestTransactionTopic, Produced.with(stringSerde, jsonNodeSerde));
//
//        // Map down to the workforce data only and provide that to the output topic.
//        KStream<String, JsonNode> outputStream =
//            partitionedOutputStream[1].map((KeyValueMapper<String, JsonNode, KeyValue<String, JsonNode>>) (key, value) -> {
//                KeyValue<String, JsonNode> pair = new KeyValue<>(value.snapshot.id, value.snapshot);
//                return pair;
//            });
//
//        // Write the workforce data output to the output topic.
//        outputStream.to(appConfig.changeRequestOutputTopic, Produced.with(stringSerde, jsonNodeSerde));
//    }
//
//    @Autowired
//    private IMergeService _mergeService;
//
//    private static final String TAG = JsonNodeTopology.class.getName();
//}
//*/
