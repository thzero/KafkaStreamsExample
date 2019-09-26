package com.example.kafka.service.communication;

import com.example.kafka.data.WorkforceChangeRequestData;

public interface ICommunicationService {
    void greet(String greeting);
    void transaction(String key, String value);
    void transaction(String key, WorkforceChangeRequestData value);
}
