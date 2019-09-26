package com.example.kafka.service.update;

import java.util.List;
import java.util.Optional;

import org.joda.time.DateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;

import org.springframework.stereotype.Service;

import com.example.kafka.data.ChangeTypes;
import com.example.kafka.data.WorkforceChangeRequestData;
import com.example.kafka.data.WorkforceData;
import com.example.kafka.response.ISuccessResponse;
import com.example.kafka.service.BaseService;
import com.example.kafka.service.IActionMergeService;
import com.example.kafka.service.IUpdateActionMergeService;

@Service
public class UpdateActionMergeService extends BaseService implements IActionMergeService {
    private static final Logger logger = LoggerFactory.getLogger(UpdateActionMergeService.class);

    @Override
    public ChangeTypes getChangeTypeCd() {
        return ChangeTypes.Update;
    }

    @Override
    public ISuccessResponse merge(WorkforceData workforce, @NonNull WorkforceChangeRequestData changeRequest) {
        try {
            ISuccessResponse response = valid(changeRequest);
            if (!response.isSuccess())
                return error(response);

            if (workforce == null)
                return error("Invalid workforce object.");

            Optional<IUpdateActionMergeService> service = _services.stream().filter(l -> l.getChangeSubTypeCd() == changeRequest.changeSubTypeCd).findFirst();
            if (!service.isPresent()) {
                logger.error(String.format("Invalid service for changeType '%s', changeSubTypeCd '%s'.", changeRequest.changeTypeCd.toString(), changeRequest.changeSubTypeCd.toString()));
                return error();
            }

            service.get().update(workforce, changeRequest);
            changeRequest.snapshot = workforce;
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
    public ISuccessResponse valid(WorkforceChangeRequestData changeRequest) {
        try {
            if (changeRequest.request == null)
                return error("Invalid 'request' element.");

            Optional<IUpdateActionMergeService> service = _services.stream().filter(l -> l.getChangeSubTypeCd() == changeRequest.changeSubTypeCd).findFirst();
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
    private List<IUpdateActionMergeService> _services;

    private static final String TAG = UpdateActionMergeService.class.getName();
}
