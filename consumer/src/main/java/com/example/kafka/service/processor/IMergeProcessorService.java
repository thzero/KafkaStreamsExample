package com.example.kafka.service.processor;

import com.example.kafka.data.WorkforceChangeRequestData;
import com.example.kafka.service.consumer.IConsumerService;

public interface IMergeProcessorService extends IConsumerService {
    boolean process(String key, WorkforceChangeRequestData changeRequest) throws Exception;
}
