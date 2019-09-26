package com.example.kafka.service.update;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.example.kafka.data.ChangeSubTypes;
import com.example.kafka.data.WorkforceChangeRequestData;
import com.example.kafka.data.WorkforceData;
import com.example.kafka.response.ISuccessResponse;
import com.example.kafka.service.BaseActionMergeService;
import com.example.kafka.service.IUpdateActionMergeService;

@Service
public class NameUpdateActionMergeService extends BaseActionMergeService implements IUpdateActionMergeService {
    @Override
    public ChangeSubTypes getChangeSubTypeCd() {
        return ChangeSubTypes.Name;
    }

    @Override
    public ISuccessResponse update(WorkforceData workforce, @NonNull WorkforceChangeRequestData changeRequest) {
        ISuccessResponse response = valid(changeRequest);
        if (!response.isSuccess())
            return error(response);

        if (workforce == null)
            return error("Invalid workforce object.");

        if (!StringUtils.isEmpty(changeRequest.request.firstName))
            workforce.firstName = changeRequest.request.firstName;
        if (!StringUtils.isEmpty(changeRequest.request.lastName))
            workforce.lastName = changeRequest.request.lastName;

        return success();
    }

    @Override
    public ISuccessResponse valid(@NonNull WorkforceChangeRequestData changeRequest) {
        ISuccessResponse response =super.valid(changeRequest);
        if (!response.isSuccess())
            return response;

        if (!StringUtils.isEmpty(changeRequest.request.firstName) || !StringUtils.isEmpty(changeRequest.request.lastName))
            return success();

        return error("Invalid 'request.firstName' or 'request.lastName' element - one or both must not be empty.");
    }
}
