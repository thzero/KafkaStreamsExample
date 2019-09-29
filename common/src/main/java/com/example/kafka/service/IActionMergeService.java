package com.example.kafka.service;

import org.springframework.lang.NonNull;

import com.example.kafka.data.ChangeTypes;
import com.example.kafka.data.WorkforceChangeRequestData;
import com.example.kafka.data.WorkforceData;
import com.example.kafka.response.ISuccessResponse;
import com.example.kafka.response.service.MergeResponse;

public interface IActionMergeService extends IService {
    ChangeTypes getChangeTypeCd();
    MergeResponse merge(WorkforceData workforce, @NonNull WorkforceChangeRequestData changeRequest);
    ISuccessResponse valid(@NonNull WorkforceChangeRequestData changeRequest);
}
