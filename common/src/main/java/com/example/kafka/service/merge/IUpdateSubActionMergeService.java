package com.example.kafka.service.merge;

import org.springframework.lang.NonNull;

import com.example.kafka.data.WorkforceChangeRequestData;
import com.example.kafka.data.WorkforceData;
import com.example.kafka.response.ISuccessResponse;

public interface IUpdateSubActionMergeService extends ISubActionMergeService {
    ISuccessResponse update(WorkforceData workforce, @NonNull WorkforceChangeRequestData changeRequest);
}
