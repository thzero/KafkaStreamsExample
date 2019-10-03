package com.example.kafka.request;

import org.springframework.lang.NonNull;

import com.example.kafka.data.WorkforceData;

public class SaveStoreWorkforceRequest extends BaseRequest {
    public SaveStoreWorkforceRequest(@NonNull WorkforceData data) {
        this.data = data;
    }

    public WorkforceData data;
}
