//package com.example.demo.serde;
//
//import java.util.Map;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import org.apache.kafka.common.errors.SerializationException;
//import org.apache.kafka.common.serialization.Deserializer;
//
///**
// * JSON deserializer for Jackson's JsonNode tree model. Using the tree model allows it to work with arbitrarily
// * structured data without having associated Java classes. This deserializer also supports Connect schemas.
// */
//public class JsonDeserializer<T> implements Deserializer<T> {
//    private ObjectMapper objectMapper = new ObjectMapper();
//
//    public JsonDeserializer() {
//    }
//
//    @Override
//    public void configure(Map<String, ?> props, boolean isKey) {
//    }
//
//    @Override
//    public T deserialize(String topic, byte[] bytes) {
//        if (bytes == null)
//            return null;
//
//        T data;
//        try {
//            data = (T)objectMapper.readTree(bytes);
//        }
//        catch (Exception e) {
//            throw new SerializationException(e);
//        }
//
//        return data;
//    }
//
//    @Override
//    public void close() {
//    }
//}
//
