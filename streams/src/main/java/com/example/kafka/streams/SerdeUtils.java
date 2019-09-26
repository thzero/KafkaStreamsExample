package com.example.kafka.streams;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;

import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.example.kafka.data.WorkforceChangeRequestData;
import com.example.kafka.data.WorkforceData;

public class SerdeUtils {
    public static Serde<String> generateString() {
        Serde<String> serde = Serdes.String();
        return serde;
    }

    public static Serde<WorkforceData> generateWorkforce() {
        Deserializer<WorkforceData> jsonDeserializer = new JsonDeserializer<>(WorkforceData.class);
        Serializer<WorkforceData> jsonSerializer = new JsonSerializer<>();
        Serde<WorkforceData> serde = Serdes.serdeFrom(jsonSerializer, jsonDeserializer);
        return serde;
    }

    public static Serde<WorkforceChangeRequestData> generateWorkforceChangeRequest() {
        Deserializer<WorkforceChangeRequestData> jsonDeserializer = new JsonDeserializer<>(WorkforceChangeRequestData.class);
        Serializer<WorkforceChangeRequestData> jsonSerializer = new JsonSerializer<>();
        Serde<WorkforceChangeRequestData> serde = Serdes.serdeFrom(jsonSerializer, jsonDeserializer);
        return serde;
    }
}
