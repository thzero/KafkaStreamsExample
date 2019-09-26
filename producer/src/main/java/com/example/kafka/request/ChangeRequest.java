package com.example.kafka.request;

import java.util.ArrayList;
import java.util.List;

import com.example.kafka.data.WorkforceChangeRequestData;

public class ChangeRequest extends BaseRequest {
    public List<WorkforceChangeRequestData> changesRequests = new ArrayList<>();
}
