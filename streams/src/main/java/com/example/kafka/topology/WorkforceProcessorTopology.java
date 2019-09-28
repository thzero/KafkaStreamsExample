package com.example.kafka.topology;

import com.fasterxml.jackson.databind.JsonNode;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.connect.json.JsonDeserializer;
import org.apache.kafka.connect.json.JsonSerializer;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;

import com.example.kafka.config.AppConfig;
import com.example.kafka.data.WorkforceChangeRequestData;
import com.example.kafka.data.WorkforceData;

public abstract class WorkforceStreamsBuilderTopology extends WorkforceBaseTopology {
    @Override
    protected Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        defineTopology(builder);

        return builder.build();
    }

    protected abstract void defineTopology(@NonNull StreamsBuilder builder);

    protected Deserializer<JsonNode> jsonDeserializer = new JsonDeserializer();
    protected Serializer<JsonNode> jsonSerializer = new JsonSerializer();
    protected Serde<JsonNode> jsonNodeSerde;
    protected Serde<String> stringSerde;
    protected Serde<WorkforceData> workforceSerde;
    protected Serde<WorkforceChangeRequestData> workforceChangeRequestSerde;


    @Autowired
    protected AppConfig appConfig;
}
