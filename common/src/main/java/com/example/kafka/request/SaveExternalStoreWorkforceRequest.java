package com.example.kafka.request;

import org.springframework.lang.NonNull;

import com.example.kafka.data.WorkforceChangeRequestData;

public class SaveExternalStoreWorkforceRequest extends BaseRequest {
    public SaveExternalStoreWorkforceRequest(@NonNull WorkforceChangeRequestData data) {
        this.data = data;
    }

    public WorkforceChangeRequestData data;
}
