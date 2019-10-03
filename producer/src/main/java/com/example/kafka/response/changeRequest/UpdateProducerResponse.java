package com.example.kafka.response.changeRequest;

import java.util.ArrayList;
import java.util.List;

import com.example.kafka.data.WorkforceChangeRequestData;
import com.example.kafka.response.ServiceResponse;

public class UpdateProducerResponse extends ServiceResponse {
    public List<WorkforceChangeRequestData> changesRequests = new ArrayList<>();
}
