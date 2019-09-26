package com.example.kafka.service.create;

import org.joda.time.DateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.example.kafka.data.ChangeTypes;
import com.example.kafka.data.WorkforceChangeRequestData;
import com.example.kafka.data.WorkforceData;
import com.example.kafka.response.ISuccessResponse;
import com.example.kafka.service.BaseService;
import com.example.kafka.service.IActionMergeService;

@Service
public class CreateActionMergeService extends BaseService implements IActionMergeService {
    private static final Logger logger = LoggerFactory.getLogger(CreateActionMergeService.class);

    @Override
    public ChangeTypes getChangeTypeCd() {
        return ChangeTypes.Create;
    }

    @Override
    public ISuccessResponse merge(WorkforceData workforce, @NonNull WorkforceChangeRequestData changeRequest) {
        try {
            ISuccessResponse response = valid(changeRequest);
            if (!response.isSuccess())
                return error(response);

            changeRequest.snapshot = changeRequest.request;
            DateTime now = DateTime.now();
            changeRequest.snapshot.lastUpdatedDate = now.toDate();
            changeRequest.snapshot.lastUpdatedTimestamp = now.getMillis();
            return success();
        }
        catch (Exception ex) {
            logger.error(TAG, ex);
        }

        return error();
    }

    @Override
    public ISuccessResponse valid(@NonNull WorkforceChangeRequestData changeRequest) {
        if (changeRequest.request == null)
            return error("Invalid 'request' element.");

        return success();
    }

    private static final String TAG = CreateActionMergeService.class.getName();
}
