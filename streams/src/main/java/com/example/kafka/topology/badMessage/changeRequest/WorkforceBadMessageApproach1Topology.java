package com.example.kafka.topology.badMessage.changeRequest;

import java.util.Arrays;

import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serde;
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

@Component("workforceBadMessageApproach1Topology")
public class WorkforceBadMessageApproach1Topology implements ITopology {
    private static final Logger logger = LoggerFactory.getLogger(WorkforceBadMessageApproach1Topology.class);

    @Override
    public Topology defineTopology(){
        Serde<WorkforceChangeRequestData> jsonSerde = SerdeUtils.generateWorkforceChangeRequest();
        Serde<String> stringSerde = SerdeUtils.generateString();

        StreamsBuilder builder = new StreamsBuilder();

        // Input stream from the input topic.
        KStream<String, String> inputStream = builder.stream(_appConfig.changeRequestTopic);

        // Read input stream... using option1 from handling bad messages...
        // https://stackoverflow.com/questions/42666756/handling-bad-messages-using-kafkas-streams-api
        // Note how the returned stream is of type KStream<String, WorkforceData>, rather than KStream<String, String>.
        KStream<String, WorkforceChangeRequestData> serializedStream = inputStream.flatMapValues((ValueMapper<String, Iterable<WorkforceChangeRequestData>>) value -> {
            try {
                // Attempt deserialization
                WorkforceChangeRequestData obj = jsonSerde.deserializer().deserialize(_appConfig.changeRequestTopic, value.getBytes());

                logger.debug("serializedStream received Payload: {} | Record: {}", obj, value.toString());

                // Ok, the record is valid (not corrupted).  Let's take the
                // opportunity to also process the record in some way so that
                // we haven't paid the deserialization cost just for "poison pill"
                // checking.
                return Arrays.asList(obj);
            }
            catch (SerializationException ex) {
                // log + ignore/skip the corrupted message
                logger.error("Could not deserialize the record", ex);
            }

            return null;
        });

        // Setup output topic... this is where a Kafka Connect would pull from for loading into Mongo (or any other Kafka Connect datasource)
        serializedStream.to(_appConfig.changeRequestOutputTopic, Produced.with(stringSerde, jsonSerde));

        return builder.build();
    }

    @Autowired
    private AppConfig _appConfig;

    private static final String TAG = WorkforceBadMessageApproach1Topology.class.getName();
}
