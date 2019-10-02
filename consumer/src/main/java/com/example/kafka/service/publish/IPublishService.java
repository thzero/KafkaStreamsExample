package com.example.kafka.service.publish;

import com.example.kafka.service.IService;

public interface IPublishService extends IService {
    void publish(String topic, String key, Object value);
}
