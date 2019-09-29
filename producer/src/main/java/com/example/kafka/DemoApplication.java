package com.example.kafka;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.example.kafka.config.BaseConfig;

@SpringBootApplication
public class DemoApplication {
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	// Producer configuration
	@Bean
	public Map<String, Object> producerConfigs() {
		Map<String, Object> props = new HashMap<>(_kafkaProperties.buildProducerProperties());
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
		return props;
	}

	@Bean
	public ProducerFactory<String, Object> producerFactory() {
		return new DefaultKafkaProducerFactory<>(producerConfigs());
	}

	@Bean
	public KafkaTemplate<String, Object> kafkaTemplate() {
		return new KafkaTemplate<>(producerFactory());
	}

	@Bean
	public NewTopic changeRequestTopic() {
		return new NewTopic(_topicNameConfig.changeRequestTopic, 3, (short) 1);
	}

	@Bean
	public NewTopic changeRequestCheckpointTopic() {
		return new NewTopic(_topicNameConfig.changeRequestCheckpointTopic, 3, (short) 1);
	}

	@Bean
	public NewTopic changeDeadLetterTopic() {
		return new NewTopic(_topicNameConfig.changeRequestDeadLetterTopic, 3, (short) 1);
	}

	@Bean
	public NewTopic changeRequestOutputTopic() {
		return new NewTopic(_topicNameConfig.changeRequestOutputTopic, 3, (short) 1);
	}

	@Bean
	public NewTopic changeRequestTransactionInternalTopic() {
		return new NewTopic(_topicNameConfig.changeRequestTransactionInternalTopic, 3, (short) 1);
	}

	@Bean
	public NewTopic changeRequestTransactionTopic() {
		return new NewTopic(_topicNameConfig.changeRequestTransactionTopic, 3, (short) 1);
	}

	@Bean
	public NewTopic deadLetterTopic() {
		return new NewTopic(_topicNameConfig.deadLetterTopic, 3, (short) 1);
	}

	@Bean
	public NewTopic loadTopic() {
		return new NewTopic(_topicNameConfig.loadTopic, 3, (short) 1);
	}

	@Bean
	public NewTopic loadOutputTopic() {
		return new NewTopic(_topicNameConfig.loadOutputTopic, 3, (short) 1);
	}

	@Bean
	public NewTopic outputTopic() {
		return new NewTopic(_topicNameConfig.outputTopic, 3, (short) 1);
	}

	@Autowired
	private KafkaProperties _kafkaProperties;

	@Autowired
	private BaseConfig _topicNameConfig;
}
