package com.example.kafka.request.publish;

import javax.validation.constraints.NotBlank;

import org.springframework.lang.NonNull;

import com.example.kafka.data.WorkforceChangeRequestData;
import com.example.kafka.request.BaseRequest;

public class WorkforceChangeRequestPublishRequest extends BaseRequest {
    public WorkforceChangeRequestPublishRequest() {}
    public WorkforceChangeRequestPublishRequest(@NonNull @NotBlank String topic, @NonNull @NotBlank String key, @NonNull @NotBlank WorkforceChangeRequestData value) {
        this.topic = topic;
        this.key = key;
        this.value = value;
    }

    public String topic;
    public String key;
    public WorkforceChangeRequestData value;
}
