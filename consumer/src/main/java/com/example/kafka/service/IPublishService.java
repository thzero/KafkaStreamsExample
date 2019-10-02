package com.example.kafka.service;

public interface IPublishService extends IService {
    void publish(String topic, String key, Object value);
}
