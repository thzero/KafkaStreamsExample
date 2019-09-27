package com.example.kafka.service.update;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.example.kafka.data.ChangeSubTypes;
import com.example.kafka.data.WorkforceChangeRequestData;
import com.example.kafka.data.WorkforceData;
import com.example.kafka.response.ISuccessResponse;
import com.example.kafka.service.BaseActionMergeService;
import com.example.kafka.service.IUpdateSubActionMergeService;

@Service
public class CompanyUpdateSubActionMergeService extends BaseActionMergeService implements IUpdateSubActionMergeService {
    @Override
    public ChangeSubTypes getChangeSubTypeCd() {
        return ChangeSubTypes.Company;
    }

    @Override
    public ISuccessResponse update(WorkforceData workforce, @NonNull WorkforceChangeRequestData changeRequest) {
        ISuccessResponse response = valid(changeRequest);
        if (!response.isSuccess())
            return error(response);

        if (workforce == null)
            return error("Invalid workforce object.");

        workforce.company = changeRequest.request.company;
        return success();
    }

    @Override
    public ISuccessResponse valid(@NonNull WorkforceChangeRequestData changeRequest) {
        return super.valid(changeRequest);
    }
}
