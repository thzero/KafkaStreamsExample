package com.example.kafka.service;

import java.util.List;
import java.util.Optional;

import org.joda.time.DateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import com.example.kafka.data.WorkforceChangeRequestData;
import com.example.kafka.data.WorkforceData;
import com.example.kafka.response.ISuccessResponse;
import com.example.kafka.response.SuccessResponse;

@Service
public class MergeService extends BaseService implements IMergeService {
    private static final Logger logger = LoggerFactory.getLogger(MergeService.class);

    public ISuccessResponse merge(@NonNull JsonNode changeRequest, @NonNull JsonNode workforce) {
        // TODO
        return new SuccessResponse();
    }

    public ISuccessResponse merge(@NonNull WorkforceChangeRequestData changeRequest, WorkforceData workforce) {
        try {
            ISuccessResponse response = valid(changeRequest);
            if (!response.isSuccess()) {
                return error(response);
            }

            Optional<IActionMergeService> service = _services.stream().filter(l -> l.getChangeTypeCd() == changeRequest.changeTypeCd).findFirst();
            if (!service.isPresent()) {
                logger.error(String.format("Invalid service for changeTypeCd '%s'.", changeRequest.changeTypeCd.toString()));
                return error();
            }

            response = service.get().merge(workforce, changeRequest);
            DateTime now = DateTime.now();
            changeRequest.processedDate = now.toDate();
            changeRequest.processedTimestamp = now.getMillis();
            return response;
        }
        catch (Exception ex) {
            logger.error(TAG, ex);
        }

        return error();
    }

    public ISuccessResponse valid(@NonNull WorkforceChangeRequestData changeRequest) {
        try {
            Optional<IActionMergeService> service = _services.stream().filter(l -> l.getChangeTypeCd() == changeRequest.changeTypeCd).findFirst();
            if (!service.isPresent()) {
                logger.error(String.format("Invalid service for changeTypeCd '%s'.", changeRequest.changeTypeCd.toString()));
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
    private List<IActionMergeService> _services;

    private static final String TAG = MergeService.class.getName();
}
