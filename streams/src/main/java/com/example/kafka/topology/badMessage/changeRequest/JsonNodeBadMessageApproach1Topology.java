package com.example.kafka.topology.badMessage.changeRequest;

import com.example.kafka.topology.JsonNodeBaseTopology;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component("jsonNodeBadMessageApproach1Topology")
public class JsonNodeBadMessageApproach1Topology extends JsonNodeBaseTopology {
    private static final Logger logger = LoggerFactory.getLogger(JsonNodeBadMessageApproach1Topology.class);

    @Override
    protected void defineTopology(StreamsBuilder builder) {
        // Input stream from the input topic.
        KStream<String, String> inputStream = builder.stream(appConfig.changeRequestTopic);

        // Read input stream... using option1 from handling bad messages...
        // https://stackoverflow.com/questions/42666756/handling-bad-messages-using-kafkas-streams-api
        // Note how the returned stream is of type KStream<String, JsonNode>, rather than KStream<String, String>.
        KStream<String, JsonNode> serializeStream = inputStream.flatMap(
            (k, v) -> {
                try {
                    // Attempt deserialization
                    JsonNode value = jsonNodeSerde.deserializer().deserialize(appConfig.changeRequestTopic, v.getBytes());

                    logger.debug("serializeStream received key '{}' | Payload: {} | Record: {}", k, v, value.toString());

                    // Ok, the record is valid (not corrupted).  Let's take the
                    // opportunity to also process the record in some way so that
                    // we haven't paid the deserialization cost just for "poison pill"
                    // checking.
                    return Collections.singletonList(KeyValue.pair(k, value));
                }
                catch (SerializationException ex) {
                    // log + ignore/skip the corrupted message
                    logger.error("Could not deserialize the record", ex);
                }

                return Collections.emptyList();
            }
        );

        // Setup output topic... this is where a Kafka Connect would pull from for loading into Mongo (or any other Kafka Connect datasource)
        serializeStream.to(appConfig.changeRequestTopic, Produced.with(stringSerde, jsonNodeSerde));
    }

    private static final String TAG = JsonNodeBadMessageApproach1Topology.class.getName();
}
