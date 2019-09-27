package com.example.kafka.response;

import com.example.kafka.data.WorkforceChangeRequestData;

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
