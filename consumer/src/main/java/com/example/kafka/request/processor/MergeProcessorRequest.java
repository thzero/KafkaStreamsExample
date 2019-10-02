package com.example.kafka.request.processor;

import javax.validation.constraints.NotBlank;

import org.springframework.lang.NonNull;

import com.example.kafka.data.WorkforceChangeRequestData;

public class MergeProcessorRequest extends ProcessorRequest {
    public MergeProcessorRequest() {}
    public MergeProcessorRequest(@NonNull @NotBlank String key, @NonNull WorkforceChangeRequestData changeRequest) {
        this.key = key;
        this.changeRequest = changeRequest;
    }

    public String key;
    public WorkforceChangeRequestData changeRequest;
}
