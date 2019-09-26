package com.example.kafka.response;

import java.util.ArrayList;
import java.util.List;

import com.example.kafka.data.WorkforceChangeRequestData;

public class UpdateProducerResponse extends SuccessResponse {
    public List<WorkforceChangeRequestData> changesRequests = new ArrayList<>();
}
