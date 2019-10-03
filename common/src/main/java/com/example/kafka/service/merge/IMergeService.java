package com.example.kafka.service.merge;

import org.springframework.lang.NonNull;

import com.fasterxml.jackson.databind.JsonNode;

import com.example.kafka.data.WorkforceChangeRequestData;
import com.example.kafka.data.WorkforceData;
import com.example.kafka.response.ISuccessResponse;
import com.example.kafka.response.merge.MergeResponse;
import com.example.kafka.service.IService;

public interface IMergeService extends IService {
    MergeResponse merge(@NonNull JsonNode changeRequest, JsonNode workforce);
    MergeResponse merge(@NonNull WorkforceChangeRequestData changeRequest, WorkforceData workforce);
    ISuccessResponse valid(@NonNull WorkforceChangeRequestData changeRequest);
}
