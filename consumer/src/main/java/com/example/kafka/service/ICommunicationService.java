package com.example.kafka.service;

import com.example.kafka.data.WorkforceChangeRequestData;

public interface ICommunicationService extends IService {
    void greet(String greeting);
    void transaction(String key, String value);
    void transaction(String key, WorkforceChangeRequestData value);
}