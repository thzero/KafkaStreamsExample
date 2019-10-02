package com.example.kafka.service.processor;

import org.springframework.lang.NonNull;

import com.example.kafka.request.processor.MergeProcessorRequest;
import com.example.kafka.response.processor.MergeProcessorResponse;

public interface IMergeProcessorService extends IProcessorService {
    MergeProcessorResponse process(@NonNull MergeProcessorRequest request);
}
