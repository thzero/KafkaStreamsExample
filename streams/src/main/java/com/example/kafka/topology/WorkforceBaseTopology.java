package com.example.kafka.topology;

import com.fasterxml.jackson.databind.JsonNode;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.connect.json.JsonDeserializer;
import org.apache.kafka.connect.json.JsonSerializer;
import org.apache.kafka.streams.Topology;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.kafka.config.AppConfig;
import com.example.kafka.data.WorkforceChangeRequestData;
import com.example.kafka.data.WorkforceData;
import com.example.kafka.SerdeUtils;

public abstract class WorkforceBaseTopology implements ITopology {
    @Override
    public Topology defineTopology() {
        jsonNodeSerde = Serdes.serdeFrom(jsonSerializer, jsonDeserializer);
        stringSerde = SerdeUtils.generateString();
        workforceSerde = SerdeUtils.generateWorkforce();
        workforceChangeRequestSerde = SerdeUtils.generateWorkforceChangeRequest();

        return buildTopology();
    }

    protected abstract Topology buildTopology();

    protected Deserializer<JsonNode> jsonDeserializer = new JsonDeserializer();
    protected Serializer<JsonNode> jsonSerializer = new JsonSerializer();
    protected Serde<JsonNode> jsonNodeSerde;
    protected Serde<String> stringSerde;
    protected Serde<WorkforceData> workforceSerde;
    protected Serde<WorkforceChangeRequestData> workforceChangeRequestSerde;

    @Autowired
    protected AppConfig appConfig;
}
