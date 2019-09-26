//package com.example.kafka.topology.advanced;
//
//import com.example.kafka.topology.ITopology;
//import com.fasterxml.jackson.databind.JsonNode;
//import org.apache.kafka.common.errors.SerializationException;
//import org.apache.kafka.common.serialization.Deserializer;
//import org.apache.kafka.common.serialization.Serde;
//import org.apache.kafka.common.serialization.Serdes;
//import org.apache.kafka.common.serialization.Serializer;
//import org.apache.kafka.connect.json.JsonDeserializer;
//import org.apache.kafka.connect.json.JsonSerializer;
//import org.apache.kafka.streams.KeyValue;
//import org.apache.kafka.streams.StreamsBuilder;
//import org.apache.kafka.streams.Topology;
//import org.apache.kafka.streams.kstream.KStream;
//import org.apache.kafka.streams.kstream.Produced;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import java.util.Collections;
//
//@Component("exampleTopology")
//public class ExampleTopology implements ITopology {
//    private static final Logger logger = LoggerFactory.getLogger(ExampleTopology.class);
//
//    @Override
//    public Topology defineTopology(){
//        StreamsBuilder builder = new StreamsBuilder();
//
//        Deserializer<JsonNode> jsonDeserializer = new JsonDeserializer();
//        Serializer<JsonNode> jsonSerializer = new JsonSerializer();
//        Serde<JsonNode> jsonSerde = Serdes.serdeFrom(jsonSerializer, jsonDeserializer);
//        Serde<String> stringSerde = Serdes.String();
//
//        // Input stream from the input topic.
//        KStream<String, String> inputStream = builder.stream(_inputTopic);
//
//        // Read input stream... using option1 from handling bad messages...
//        // https://stackoverflow.com/questions/42666756/handling-bad-messages-using-kafkas-streams-api
//        // Note how the returned stream is of type KStream<String, JsonNode>, rather than KStream<String, String>.
//        KStream<String, JsonNode> serializeStream = inputStream.flatMap(
//                (k, v) -> {
//                    try {
//                        // Attempt deserialization
//                        JsonNode value = jsonSerde.deserializer().deserialize(_inputTopic, v.getBytes());
//
//                        logger.debug("Logger 2 received key {} | Payload: {} | Record: {}", k, v, value.toString());
//
//                        // Ok, the record is valid (not corrupted).  Let's take the
//                        // opportunity to also process the record in some way so that
//                        // we haven't paid the deserialization cost just for "poison pill"
//                        // checking.
//                        return Collections.singletonList(KeyValue.pair(k, value));
//                    }
//                    catch (SerializationException e) {
//                        // log + ignore/skip the corrupted message
//                        System.err.println("Could not deserialize record: " + e.getMessage());
//                    }
//
//                    return Collections.emptyList();
//                }
//        );
//
////        KStream<String, Integer> intStream = inputStream.flatMapValues(s->{
////            try {
////                return Collections.singletonList(Integer.valueOf(s));
////            }
////            catch (Exception e){
////                System.out.println("Can't parse message: " + e.toString());
////                return Collections.emptyList();
////            }
////        });
//
////        KTable<Windowed<String>, Integer> groupedMetrics = intStream.groupBy((key, value)->key,
////                Serialized.with(Serdes.String(),Serdes.Integer())).windowedBy(TimeWindows.of(TimeUnit.SECONDS.toMillis(_windowsSizeSeconds))).aggregate(
////                ()-> 0,
////                (String aggKey, Integer newValue, Integer aggValue)->{
////                    Integer val = aggValue+newValue;
////                    return val;
////                },
////                Materialized.<String,Integer, WindowStore<Bytes,byte[]>>as("GROUPING.WINDOW").withKeySerde(Serdes.String()).withValueSerde(Serdes.Integer())
////        );
////
////        groupedMetrics.toStream().map((key,value)-> {
////            System.out.println(key.key()+":"+value.toString());
////            return KeyValue.pair(key.key(),value.toString());
////        }).to(_metricsOutputTopic);
//
//        serializeStream.to(_outputTopic, Produced.with(stringSerde, jsonSerde));

//        // Write the Workforce data, including the change types, to the transaction table
////        https://docs.confluent.io/current/streams/faq.html#how-can-i-convert-a-kstream-to-a-ktable-without-an-aggregation-step
//        final Materialized<String, WorkforceData, KeyValueStore<Bytes, byte[]>> materialized = Materialized.as(_transactionTable);
////        final KTable<String, WorkforceData> transactionTable = serializedStream.groupByKey().reduce((a, v) -> v, materialized);
//        final KTable<String, WorkforceData> transactionTable = serializedStream.groupByKey(Serialized.with(stringSerde, jsonSerde)).reduce((a, v) -> v, materialized);
//
//        // Setup output topic... this is where a Kafka Connect would pull from for loading into Mongo (or any other Kafka Connect datasource)
//        transactionTable.toStream().to(_outputTopic, Produced.with(stringSerde, jsonSerde));

// Setup output topic... this is where a Kafka Connect would pull from for loading into Mongo (or any other Kafka Connect datasource)
//        serializedStream.to(_outputTopic, Produced.with(stringSerde, jsonSerde));
//
//        return builder.build();
//    }
//
//    @Value("${tpd.topic-input.name}")
//    public String _inputTopic;
//
//    @Value("${tpd.topic-output.name}")
//    public String _outputTopic;
//
//    @Value("${tpd.topic-input.aggregation.window.size.secs}")
//    public Integer _windowsSizeSeconds;
//}
