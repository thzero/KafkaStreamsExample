package com.example.kafka.service.merge.update;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;

import org.springframework.stereotype.Service;

import com.example.kafka.DateUtils;
import com.example.kafka.data.ChangeTypes;
import com.example.kafka.data.WorkforceChangeRequestData;
import com.example.kafka.data.WorkforceData;
import com.example.kafka.response.ISuccessResponse;
import com.example.kafka.response.merge.MergeResponse;
import com.example.kafka.service.BaseService;
import com.example.kafka.service.merge.IActionMergeService;
import com.example.kafka.service.merge.IUpdateSubActionMergeService;

@Service
public class UpdateActionMergeService extends BaseService implements IActionMergeService {
    private static final Logger logger = LoggerFactory.getLogger(UpdateActionMergeService.class);

    @Override
    public ChangeTypes getChangeTypeCd() {
        return ChangeTypes.Update;
    }

    @Override
    public MergeResponse merge(WorkforceData workforce, @NonNull WorkforceChangeRequestData changeRequest) {
        MergeResponse response = new MergeResponse();
        try {
            ISuccessResponse validResponse = valid(changeRequest);
            if (!validResponse.isSuccess())
                return error(response, validResponse);

            if (workforce == null)
                return error(response, "Invalid workforce object.");

            Optional<IUpdateSubActionMergeService> service = _services.stream().filter(l -> l.getChangeSubTypeCd() == changeRequest.changeSubTypeCd).findFirst();
            if (!service.isPresent()) {
                logger.error(String.format("Invalid service for changeType '%s', changeSubTypeCd '%s'.", changeRequest.changeTypeCd.toString(), changeRequest.changeSubTypeCd.toString()));
                return error(response);
            }

            service.get().update(workforce, changeRequest);
            changeRequest.snapshot = workforce;
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
    public ISuccessResponse valid(WorkforceChangeRequestData changeRequest) {
        try {
            if (changeRequest.request == null)
                return error("Invalid 'request' element.");

            Optional<IUpdateSubActionMergeService> service = _services.stream().filter(l -> l.getChangeSubTypeCd() == changeRequest.changeSubTypeCd).findFirst();
            if (!service.isPresent()) {
                logger.error(String.format("Invalid service for changeType '%s', changeSubTypeCd '%s'.", changeRequest.changeTypeCd.toString(), changeRequest.changeSubTypeCd.toString()));
                return error();
            }

            return service.get().valid(changeRequest);
        }
        catch (Exception ex) {
            logger.error(TAG, ex);
        }

        return error();
    }

    @Autowired
    private List<IUpdateSubActionMergeService> _services;

    private static final String TAG = UpdateActionMergeService.class.getName();
}
