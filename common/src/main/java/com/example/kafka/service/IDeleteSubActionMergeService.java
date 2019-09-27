package com.example.kafka.service;

import org.springframework.lang.NonNull;

import com.example.kafka.data.WorkforceChangeRequestData;
import com.example.kafka.data.WorkforceData;
import com.example.kafka.response.ISuccessResponse;

public interface IDeleteSubActionMergeService extends ISubActionMergeService {
    ISuccessResponse delete(WorkforceData workforce, @NonNull WorkforceChangeRequestData changeRequest);
}
