package com.example.kafka.response.service;

import com.example.kafka.data.WorkforceChangeRequestData;
import com.example.kafka.response.SuccessResponse;

public class MergeResponse extends SuccessResponse {
    public MergeResponse() {}
    public MergeResponse(boolean success) {
        super(success);
    }
    public MergeResponse(WorkforceChangeRequestData changeRequestData) {
        this.changeRequest = changeRequestData;
    }

    public WorkforceChangeRequestData changeRequest;
}
