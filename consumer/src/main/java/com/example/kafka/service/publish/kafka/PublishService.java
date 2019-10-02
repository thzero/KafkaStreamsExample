package com.example.kafka.service.publish.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.kafka.service.BaseService;
import com.example.kafka.service.IPublishService;

@Service
public class PublishService extends BaseService implements IPublishService {
    public void publish(String topic, String key, Object value) {
        _kafkaTemplate.send(topic, key, value);
    }

    @Autowired
    private KafkaTemplate<String, Object> _kafkaTemplate;
}
