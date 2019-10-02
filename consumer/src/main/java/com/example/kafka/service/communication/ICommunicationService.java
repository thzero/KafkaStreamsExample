package com.example.kafka.service.communication;

import com.example.kafka.data.WorkforceChangeRequestData;
import com.example.kafka.service.IService;

public interface ICommunicationService extends IService {
    void greet(String greeting);
    void transaction(String key, String value);
    void transaction(String key, WorkforceChangeRequestData value);
}
