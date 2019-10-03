package com.example.kafka;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.AbstractKafkaListenerContainerFactory;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.SeekToCurrentErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.example.kafka.data.WorkforceChangeRequestData;
import com.example.kafka.service.consumer.IMergeConsumerService;
import com.example.kafka.service.consumer.IGenericConsumerService;

@SpringBootApplication
public class DemoApplication {
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	// Producer configuration
	@Bean
	public Map<String, Object> producerWorkforceChangeRequestConfigs() {
		Map<String, Object> props = new HashMap<>(_kafkaProperties.buildProducerProperties());
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
		return props;
	}

	@Bean
	public ProducerFactory<String, WorkforceChangeRequestData> producerWorkforceChangeRequestFactory() {
		return new DefaultKafkaProducerFactory<>(producerWorkforceChangeRequestConfigs());
	}

	@Bean
	public KafkaTemplate<String, WorkforceChangeRequestData> kafkaWorkforceChangeRequestTemplate() {
		return new KafkaTemplate<>(producerWorkforceChangeRequestFactory());
	}

	// Consumer configuration
    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>(_kafkaProperties.buildConsumerProperties());
		// If you only need one kind of deserialization, you only need to set the
		// Consumer configuration properties. Uncomment this and remove all others below.
//        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,  StringDeserializer.class);
//        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, _groupId);

        return props;
    }

	// Object Serializer Consumer Configuration
	@Bean
	public ConsumerFactory<String, Object> consumerFactory() {
		final JsonDeserializer<Object> jsonDeserializer = new JsonDeserializer<>();
		jsonDeserializer.addTrustedPackages("*");
		return new DefaultKafkaConsumerFactory<>(_kafkaProperties.buildConsumerProperties(), new StringDeserializer(), jsonDeserializer);
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory());
//		factory.getContainerProperties().setAckOnError(false);
//		factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
//		factory.setErrorHandler(new SeekToCurrentErrorHandler());
		initializeFactory(factory);
		return factory;
	}

	// String Consumer Configuration
	@Bean
	public ConsumerFactory<String, String> stringConsumerFactory() {
		return new DefaultKafkaConsumerFactory<>(_kafkaProperties.buildConsumerProperties(), new StringDeserializer(), new StringDeserializer());
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerStringContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(stringConsumerFactory());
//		factory.getContainerProperties().setAckOnError(false);
//		factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
//		factory.setErrorHandler(new SeekToCurrentErrorHandler());
		initializeFactory(factory);
		return factory;
	}

	// Byte Array Consumer Configuration
	@Bean
	public ConsumerFactory<String, byte[]> byteArrayConsumerFactory() {
		return new DefaultKafkaConsumerFactory<>(_kafkaProperties.buildConsumerProperties(), new StringDeserializer(), new ByteArrayDeserializer()
		);
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, byte[]> kafkaListenerByteArrayContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, byte[]> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(byteArrayConsumerFactory());
//		factory.getContainerProperties().setAckOnError(false);
//		factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
//		factory.setErrorHandler(new SeekToCurrentErrorHandler());
		initializeFactory(factory);
		return factory;
	}

	private void initializeFactory(AbstractKafkaListenerContainerFactory factory) {
		factory.getContainerProperties().setAckOnError(false);
		factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
		factory.setErrorHandler(new SeekToCurrentErrorHandler());
	}

	@Autowired
	private KafkaProperties _kafkaProperties;

	@Value("${application.change-request.group-id}")
	private String _groupId;

	@Autowired
	private IGenericConsumerService _genericConsumerService;

	@Autowired
	private IMergeConsumerService _mergeConsumerService;
}
