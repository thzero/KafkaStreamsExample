package com.example.kafka.request;

import org.springframework.lang.NonNull;

import com.example.kafka.data.WorkforceData;
import com.example.kafka.request.BaseRequest;

public class SaveStoreWorkforceRequest extends BaseRequest {
    public SaveStoreWorkforceRequest(@NonNull WorkforceData data) {
        this.data = data;
    }

    public WorkforceData data;
}
