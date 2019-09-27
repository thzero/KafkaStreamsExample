package com.example.kafka.service;

import org.springframework.lang.NonNull;

import com.example.kafka.data.ChangeSubTypes;
import com.example.kafka.data.WorkforceChangeRequestData;
import com.example.kafka.response.ISuccessResponse;

public interface ISubActionMergeService {
    ChangeSubTypes getChangeSubTypeCd();
    ISuccessResponse valid(@NonNull WorkforceChangeRequestData changeRequest);
}
