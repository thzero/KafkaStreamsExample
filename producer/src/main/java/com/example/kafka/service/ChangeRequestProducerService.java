package com.example.kafka.service;

import java.time.Instant;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.kafka.DateUtils;
import com.example.kafka.config.AppConfig;
import com.example.kafka.data.WorkforceChangeRequestData;
import com.example.kafka.response.ISuccessResponse;
import com.example.kafka.response.UpdateProducerResponse;

@Service
public class ChangeRequestProducerService implements IChangeRequestProducerService {
    private static final Logger logger = LoggerFactory.getLogger(ChangeRequestProducerService.class);

    @Override
    public UpdateProducerResponse submit(List<WorkforceChangeRequestData> changesRequests) throws Exception {
        try {
            logger.debug("update - Starting workforce update change request...");

            UpdateProducerResponse response = new UpdateProducerResponse();
            int i = 0;
            for (WorkforceChangeRequestData changeRequest : changesRequests) {
                try {
                    // Use the workforce data id, not the id of the change request so that all requests for the same
                    // workforce data id get allocated to the same partition
                    logger.debug("index: '{}' | id: '{}' | data: {}", i, changeRequest.getWorkforceRequestId(), changeRequest.toString());
                    ISuccessResponse result = _mergeService.valid(changeRequest);
                    if (!result.isSuccess()) {
                        response.setResponse(result);
                        logger.debug("\tindex: '{}' | id: '{}' | error: {}", i, changeRequest.getWorkforceRequestId(), response.getError().toString());
                        continue;
                    }

                    Instant instant = Instant.now();
                    changeRequest.requestedDate = DateUtils.toDate(instant);
                    changeRequest.requestedTimestamp = DateUtils.toEpochSeconds(instant);
                    _kafkaTemplate.send(_appConfig.changeRequestTopic, changeRequest.getWorkforceRequestId(), changeRequest);
                    response.changesRequests.add(changeRequest);
                }
                catch (Exception ex) {
                    logger.error(TAG, ex);
                }
            }

            return response;
        }
        catch (Exception ex) {
            logger.error(TAG, ex);
            throw ex;
        }
        finally {
            logger.debug("update - Finished update submission of change request");
        }
    }

    @Autowired
    private KafkaTemplate<String, Object> _kafkaTemplate;

    @Autowired
    private IMergeService _mergeService;

    @Autowired
    private AppConfig _appConfig;

    private static final String TAG = ChangeRequestProducerService.class.getName();
}
