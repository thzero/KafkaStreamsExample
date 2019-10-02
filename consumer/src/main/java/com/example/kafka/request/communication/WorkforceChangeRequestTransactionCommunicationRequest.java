package com.example.kafka.request.communication;

import javax.validation.constraints.NotBlank;

import org.springframework.lang.NonNull;

import com.example.kafka.data.WorkforceChangeRequestData;
import com.example.kafka.request.BaseRequest;

public class WorkforceChangeRequestTransactionCommunicationRequest extends BaseRequest {
    public WorkforceChangeRequestTransactionCommunicationRequest() {}
    public WorkforceChangeRequestTransactionCommunicationRequest(@NonNull @NotBlank String key, @NonNull WorkforceChangeRequestData value) {
        this.key = key;
        this.value = value;
    }

    public String key;
    public WorkforceChangeRequestData value;
}
