package com.example.kafka.service.create;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.example.kafka.DateUtils;
import com.example.kafka.data.ChangeTypes;
import com.example.kafka.data.WorkforceChangeRequestData;
import com.example.kafka.data.WorkforceData;
import com.example.kafka.response.ISuccessResponse;
import com.example.kafka.response.service.MergeResponse;
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
    public MergeResponse merge(WorkforceData workforce, @NonNull WorkforceChangeRequestData changeRequest) {
        MergeResponse response = new MergeResponse();
        try {
            ISuccessResponse validResponse = valid(changeRequest);
            if (!validResponse.isSuccess())
                return error(response, validResponse);

            changeRequest.snapshot = changeRequest.request;
            Instant instant = Instant.now();
            changeRequest.snapshot.lastUpdatedDate = DateUtils.toDate(instant);
            changeRequest.snapshot.lastUpdatedTimestamp = DateUtils.toEpochSeconds(instant);
            response.changeRequest = changeRequest;
            return response;
        }
        catch (Exception ex) {
            logger.error(TAG, ex);
        }

        return error(response);
    }

    @Override
    public ISuccessResponse valid(@NonNull WorkforceChangeRequestData changeRequest) {
        if (changeRequest.request == null)
            return error("Invalid 'request' element.");

        return success();
    }

    private static final String TAG = CreateActionMergeService.class.getName();
}
