package com.example.kafka.request.publish;

import javax.validation.constraints.NotBlank;

import org.springframework.lang.NonNull;

import com.example.kafka.data.WorkforceChangeRequestData;
import com.example.kafka.request.BaseRequest;

public class WorkforceChangeRequestPublishRequest extends BaseRequest {
    public WorkforceChangeRequestPublishRequest() {}
    public WorkforceChangeRequestPublishRequest(@NonNull @NotBlank Topics topic, @NonNull @NotBlank String key, @NonNull @NotBlank WorkforceChangeRequestData value) {
        this.topic = topic;
        this.key = key;
        this.value = value;
    }

    public String key;
    public Topics topic;
    public WorkforceChangeRequestData value;

    public enum Topics {
        Checkpoint,
        DeadLetter,
        Transaction,
        TransactionRedacted
    }
}
