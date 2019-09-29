package com.example.kafka.request;

import javax.validation.constraints.NotBlank;

import org.springframework.lang.NonNull;

public class RetrieveStoreWorkforceRequest extends BaseRequest {
    public RetrieveStoreWorkforceRequest(@NonNull @NotBlank String id) {
        this.id = id;
    }

    public String id;
}
