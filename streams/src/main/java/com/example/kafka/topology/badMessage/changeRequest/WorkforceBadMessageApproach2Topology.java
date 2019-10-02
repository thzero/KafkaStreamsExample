package com.example.kafka.topology.badMessage.changeRequest;

import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.kafka.config.AppConfig;
import com.example.kafka.data.WorkforceChangeRequestData;
import com.example.kafka.SerdeUtils;
import com.example.kafka.topology.ITopology;

@Component("workforceBadMessageApproach2Topology")
public class WorkforceBadMessageApproach2Topology implements ITopology {
    private static final Logger logger = LoggerFactory.getLogger(WorkforceBadMessageApproach2Topology.class);

    @Override
    public Topology defineTopology() {
        Serde<WorkforceChangeRequestData> jsonSerde = SerdeUtils.generateWorkforceChangeRequest();
        Serde<String> stringSerde = SerdeUtils.generateString();

        StreamsBuilder builder = new StreamsBuilder();

        // Input stream from the input topic.
        KStream<String, String> inputStream = builder.stream(_appConfig.changeRequestTopic);

        // Read input stream... using option 2
        // This branches on a unserializable input
        // https://stackoverflow.com/questions/42666756/handling-bad-messages-using-kafkas-streams-api
        KStream<String, String>[] partitioned = inputStream.branch(
            (k, v) -> {
                try {
                    WorkforceChangeRequestData value = jsonSerde.deserializer().deserialize(_appConfig.changeRequestTopic, v.getBytes());
                    logger.debug("inputStream partitioning received key {} | Payload: {} | Record: {}", k, v, value.toString());
                    return true;
                }
                catch (SerializationException ignored) {
                }
                logger.debug("inputStream partitioning invalid message received key {} | Payload: {} | Record: {}", k, v, "ERROR");
                return false;
            },
            (k, v) -> true
        );

        // Bad messages - partitioned[0] is the KStream<String, String> that contains only valid records.  partitioned[1] contains only corrupted
        // records and thus acts as a "dead letter queue".
        final KStream<String, WorkforceChangeRequestData> serializedStream = partitioned[0].map(
            (k, v) -> {
                // Must deserialize a second time unfortunately.
                WorkforceChangeRequestData value = jsonSerde.deserializer().deserialize(_appConfig.changeRequestTopic, v.getBytes());

                logger.debug("serializedStream received key '{}' | Payload: {} | Record: {}", k, v, value.toString());
                KeyValue<String, WorkforceChangeRequestData> pair = KeyValue.pair(k, value);
                return pair;
            });

        // Bad messages - Don't forget to actually write the dead letter queue back to Kafka!
        partitioned[1].to(_appConfig.deadLetterTopic, Produced.with(stringSerde, stringSerde));

        // Setup output topic... this is where a Kafka Connect would pull from for loading into Mongo (or any other Kafka Connect datasource)
        partitioned[0].to(_appConfig.changeRequestOutputTopic);

        return builder.build();
    }

    @Autowired
    private AppConfig _appConfig;

    private static final String TAG = WorkforceBadMessageApproach2Topology.class.getName();
}
