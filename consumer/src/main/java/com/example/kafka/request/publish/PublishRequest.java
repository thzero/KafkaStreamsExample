package com.example.kafka.request.publish;

import javax.validation.constraints.NotBlank;

import org.springframework.lang.NonNull;

import com.example.kafka.request.BaseRequest;

public class PublishRequest extends BaseRequest {
    public PublishRequest() {}
    public PublishRequest(@NonNull @NotBlank String topic, @NonNull @NotBlank String key, @NonNull @NotBlank Object value) {
        this.topic = topic;
        this.key = key;
        this.value = value;
    }

    public String topic;
    public String key;
    public Object value;
}
