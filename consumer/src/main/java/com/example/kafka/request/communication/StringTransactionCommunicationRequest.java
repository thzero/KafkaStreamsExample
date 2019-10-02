package com.example.kafka.request.communication;

import javax.validation.constraints.NotBlank;

import org.springframework.lang.NonNull;

import com.example.kafka.request.BaseRequest;

public class StringTransactionCommunicationRequest extends BaseRequest {
    public StringTransactionCommunicationRequest() {}
    public StringTransactionCommunicationRequest(@NonNull @NotBlank String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String key;
    public String value;
}
