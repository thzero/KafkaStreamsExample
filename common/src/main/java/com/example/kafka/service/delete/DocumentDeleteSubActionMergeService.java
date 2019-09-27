package com.example.kafka.service.delete;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.example.kafka.data.ChangeSubTypes;
import com.example.kafka.data.StatusType;
import com.example.kafka.data.WorkforceChangeRequestData;
import com.example.kafka.data.WorkforceData;
import com.example.kafka.response.ISuccessResponse;
import com.example.kafka.service.BaseActionMergeService;
import com.example.kafka.service.IDeleteSubActionMergeService;

@Service
public class DocumentDeleteSubActionMergeService extends BaseActionMergeService implements IDeleteSubActionMergeService {
    @Override
    public ChangeSubTypes getChangeSubTypeCd() {
        return ChangeSubTypes.Document;
    }

    @Override
    public ISuccessResponse delete(WorkforceData workforce, @NonNull WorkforceChangeRequestData changeRequest) {
        ISuccessResponse response = valid(changeRequest);
        if (!response.isSuccess())
            return error(response);

        if (workforce == null)
            return error("Invalid workforce object.");

        workforce = new WorkforceData();
        workforce.id = changeRequest.request.id;
        workforce.status = StatusType.Deleted;
        return success();
    }

    @Override
    public ISuccessResponse valid(@NonNull WorkforceChangeRequestData changeRequest) {
        return super.valid(changeRequest);
    }
}
