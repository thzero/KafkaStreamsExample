package com.example.kafka.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import com.example.kafka.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import com.example.kafka.data.WorkforceChangeRequestData;
import com.example.kafka.data.WorkforceData;
import com.example.kafka.response.ISuccessResponse;
import com.example.kafka.response.service.MergeResponse;

@Service
public class MergeService extends BaseService implements IMergeService {
    private static final Logger logger = LoggerFactory.getLogger(MergeService.class);

    public MergeResponse merge(@NonNull JsonNode changeRequest, @NonNull JsonNode workforce) {
        // TODO
        return new MergeResponse();
    }

    public MergeResponse merge(@NonNull WorkforceChangeRequestData changeRequest, WorkforceData workforce) {
        MergeResponse response = new MergeResponse();
        try {
            ISuccessResponse responseValid = valid(changeRequest);
            if (!responseValid.isSuccess())
                return error(response);

            Optional<IActionMergeService> service = _services.stream().filter(l -> l.getChangeTypeCd() == changeRequest.changeTypeCd).findFirst();
            if (!service.isPresent()) {
                logger.error(String.format("Invalid service for changeTypeCd '%s'.", changeRequest.changeTypeCd.toString()));
                return error(response);
            }

            response = service.get().merge(workforce, changeRequest);
            Instant instant = Instant.now();
            response.changeRequest.processDate = DateUtils.toDate(instant);
            response.changeRequest.processTimestamp = DateUtils.toEpochSeconds(instant);
            return response;
        }
        catch (Exception ex) {
            logger.error(TAG, ex);
        }

        return error(response);
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
