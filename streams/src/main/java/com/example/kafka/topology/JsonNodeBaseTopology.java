package com.example.kafka.topology;

import com.fasterxml.jackson.databind.JsonNode;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.connect.json.JsonDeserializer;
import org.apache.kafka.connect.json.JsonSerializer;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;

import com.example.kafka.config.AppConfig;
import com.example.kafka.streams.SerdeUtils;

public abstract class JsonNodeBaseTopology implements ITopology {
    private static final Logger logger = LoggerFactory.getLogger(JsonNodeBaseTopology.class);

    @Override
    public Topology defineTopology() {
        jsonNodeSerde = Serdes.serdeFrom(jsonSerializer, jsonDeserializer);
        stringSerde = SerdeUtils.generateString();

        StreamsBuilder builder = new StreamsBuilder();

        defineTopology(builder);

        return builder.build();
    }

    protected abstract void defineTopology(@NonNull StreamsBuilder builder);

    protected Deserializer<JsonNode> jsonDeserializer = new JsonDeserializer();
    protected Serializer<JsonNode> jsonSerializer = new JsonSerializer();
    protected Serde<JsonNode> jsonNodeSerde;
    protected Serde<String> stringSerde;

    @Autowired
    protected AppConfig appConfig;
}
