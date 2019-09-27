package com.example.kafka.streams;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;

import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;

import com.example.kafka.data.WorkforceChangeRequestData;
import com.example.kafka.data.WorkforceData;

public class SerdeUtils {
    public static Serde<String> generateString() {
        Serde<String> serde = Serdes.String();
        return serde;
    }

    public static Serde<WorkforceData> generateWorkforce() {
        ObjectMapper mapper = initializeObjectMapper();
        Deserializer<WorkforceData> jsonDeserializer = new JsonDeserializer<>(WorkforceData.class, mapper);
        Serializer<WorkforceData> jsonSerializer = new JsonSerializer<>(mapper);
//        Deserializer<WorkforceData> jsonDeserializer = new JsonDeserializer<>(WorkforceData.class);
//        Serializer<WorkforceData> jsonSerializer = new JsonSerializer<>();
        Serde<WorkforceData> serde = Serdes.serdeFrom(jsonSerializer, jsonDeserializer);
        return serde;
    }

    public static Serde<WorkforceChangeRequestData> generateWorkforceChangeRequest() {
        ObjectMapper mapper = initializeObjectMapper();
        Deserializer<WorkforceChangeRequestData> jsonDeserializer = new JsonDeserializer<>(WorkforceChangeRequestData.class, mapper);
        Serializer<WorkforceChangeRequestData> jsonSerializer = new JsonSerializer<>(mapper);
//        Deserializer<WorkforceChangeRequestData> jsonDeserializer = new JsonDeserializer<>(WorkforceChangeRequestData.class);
//        Serializer<WorkforceChangeRequestData> jsonSerializer = new JsonSerializer<>();
        Serde<WorkforceChangeRequestData> serde = Serdes.serdeFrom(jsonSerializer, jsonDeserializer);
        return serde;
    }

    private static ObjectMapper initializeObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // StdDateFormat is ISO8601 since jackson 2.9
        mapper.setDateFormat(new StdDateFormat().withColonInTimeZone(true));
        return mapper;
    }
}
