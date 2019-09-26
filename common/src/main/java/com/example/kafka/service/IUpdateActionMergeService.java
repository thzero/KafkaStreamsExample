package com.example.kafka.service;

import org.springframework.lang.NonNull;

import com.example.kafka.data.ChangeSubTypes;
import com.example.kafka.data.WorkforceChangeRequestData;
import com.example.kafka.data.WorkforceData;
import com.example.kafka.response.ISuccessResponse;

public interface IUpdateActionMergeService {
    ChangeSubTypes getChangeSubTypeCd();
    ISuccessResponse update(WorkforceData workforce, @NonNull WorkforceChangeRequestData changeRequest);
    ISuccessResponse valid(@NonNull WorkforceChangeRequestData changeRequest);
}
