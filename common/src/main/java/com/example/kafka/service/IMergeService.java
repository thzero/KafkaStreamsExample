package com.example.kafka.service;

import org.springframework.lang.NonNull;

import com.fasterxml.jackson.databind.JsonNode;

import com.example.kafka.data.WorkforceChangeRequestData;
import com.example.kafka.data.WorkforceData;
import com.example.kafka.response.ISuccessResponse;

public interface IMergeService {
    ISuccessResponse merge(@NonNull JsonNode changeRequest, JsonNode workforce);
    ISuccessResponse merge(@NonNull WorkforceChangeRequestData changeRequest, WorkforceData workforce);
    ISuccessResponse valid(@NonNull WorkforceChangeRequestData changeRequest);
}
