package com.example.kafka.service.merge;

import org.springframework.lang.NonNull;

import com.example.kafka.data.ChangeSubTypes;
import com.example.kafka.data.WorkforceChangeRequestData;
import com.example.kafka.response.ISuccessResponse;
import com.example.kafka.service.IService;

public interface ISubActionMergeService extends IService {
    ChangeSubTypes getChangeSubTypeCd();
    ISuccessResponse valid(@NonNull WorkforceChangeRequestData changeRequest);
}
