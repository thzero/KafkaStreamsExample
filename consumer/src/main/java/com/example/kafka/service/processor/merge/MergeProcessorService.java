package com.example.kafka.service.processor.merge;

import javax.validation.constraints.NotBlank;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.example.kafka.DateUtils;
import com.example.kafka.config.AppConfig;
import com.example.kafka.data.ChangeRequestData;
import com.example.kafka.data.ChangeTypes;
import com.example.kafka.data.WorkforceChangeRequestData;
import com.example.kafka.data.WorkforceData;
import com.example.kafka.request.RetrieveStoreWorkforceRequest;
import com.example.kafka.request.SaveStoreWorkforceRequest;
import com.example.kafka.response.service.MergeResponse;
import com.example.kafka.response.service.RetrieveStoreWorkforceResponse;
import com.example.kafka.response.service.SaveStoreWorkforceResponse;
import com.example.kafka.service.BaseService;
import com.example.kafka.service.IMergeService;
import com.example.kafka.service.IPublishService;
import com.example.kafka.service.IStoreWorkforceService;
import com.example.kafka.service.processor.IMergeProcessorService;

@Service
public class MergeProcessorService extends BaseService implements IMergeProcessorService {
    private static final Logger logger = LoggerFactory.getLogger(MergeProcessorService.class);

    public boolean process(String key, WorkforceChangeRequestData changeRequest) throws Exception {
        if (changeRequest == null)
            return false;

        if (StringUtils.isEmpty(changeRequest.getWorkforceRequestId()))
            return false;

        try {
            logger.debug("request id '{}'", changeRequest.getWorkforceRequestId());

            WorkforceData workforceData = null;
            if (changeRequest.changeTypeCd != ChangeTypes.Create) {
                // Lookup the workforce data
                workforceData = loadWorkforceData(changeRequest.getWorkforceRequestId());
                if (workforceData == null) {
                    logger.warn("workforce data for request id '{}' was not found!", changeRequest.getWorkforceRequestId());
                    return false;
                }
                setProcessedStatus(changeRequest, ChangeRequestData.ProcessStatus.Found);
                publish(_appConfig.changeRequestCheckpointTopic, key, changeRequest);
            }

            logger.debug("workforce data for request id '{}' was found!", changeRequest.getWorkforceRequestId());

            // Merge the data
            logger.debug("before, key: '{}' | changeRequest: {} | joinedStream: {}", changeRequest.getWorkforceRequestId(), changeRequest.toString(), workforceData.toString());
            MergeResponse response = getMergeService().merge(changeRequest, workforceData);
            if (!response.isSuccess()) {
                logger.warn("workforce data for request id '{}' had the following error: {}", changeRequest.getWorkforceRequestId(), response.getError());
                // TODO
                // Should we throw an exception here?  That would cause an unhandled exception, which is caught the handler, and
                // shutdown the app otherwise forwarding will auto-commit.
                // Write it to the dead-letter sink
                setProcessedStatus(changeRequest, ChangeRequestData.ProcessStatus.MergeFailed);
                publish(_appConfig.changeRequestCheckpointTopic, key, changeRequest);
                publish(_appConfig.changeRequestDeadLetterTopic, key, changeRequest);
                return false;
            }
            logger.debug("after, key: '{}' | changeRequest: {}", response.changeRequest.getWorkforceRequestId(), response.changeRequest.toString());
            setProcessedStatus(changeRequest, ChangeRequestData.ProcessStatus.Merged);
            publish(_appConfig.changeRequestCheckpointTopic, key, changeRequest);

            boolean result = storeWorkforceData(key, response.changeRequest.snapshot);
            if (!result) {
                // TODO
                // Should we throw an exception here?  That would cause an unhandled exception, which is caught the handler, and
                // shutdown the app otherwise forwarding will auto-commit.
                setProcessedStatus(changeRequest, ChangeRequestData.ProcessStatus.StoreFailed);
                publish(_appConfig.changeRequestCheckpointTopic, key, changeRequest);
                return false;
            }
            setProcessedStatus(changeRequest, ChangeRequestData.ProcessStatus.Stored);
            publish(_appConfig.changeRequestCheckpointTopic, key, changeRequest);

            // Write the transaction to the transaction internal sink
            publish(_appConfig.changeRequestTransactionInternalTopic, key, changeRequest);

            // Remove the request and snapshot; do not want to send the data when announcing a transaction
            response.changeRequest.request = null;
            response.changeRequest.snapshot = null;

            // Write the transaction to the transaction external sink
            publish(_appConfig.changeRequestTransactionTopic, key, changeRequest);

            setProcessedStatus(changeRequest, ChangeRequestData.ProcessStatus.Success);
            publish(_appConfig.changeRequestCheckpointTopic, key, changeRequest);

            return true;
        }
        catch (Exception ex) {
            logger.debug(TAG, ex);
            setProcessedStatus(changeRequest, ChangeRequestData.ProcessStatus.Failed);
            publish(_appConfig.changeRequestCheckpointTopic, key, changeRequest);
            publish(_appConfig.changeRequestDeadLetterTopic, key, changeRequest);

            throw ex;
        }
    }

    protected void setProcessedStatus(@NonNull WorkforceChangeRequestData changeRequest, ChangeRequestData.ProcessStatus status) {
        Instant instant = Instant.now();
        changeRequest.processDate = DateUtils.toDate(instant);
        changeRequest.processTimestamp = DateUtils.toEpochSeconds(instant);
        changeRequest.processStatus = status;
    }

    protected IMergeService getMergeService() {
        return _mergeService;
    }

    protected WorkforceData loadWorkforceData(@NotBlank String key) {
        RetrieveStoreWorkforceResponse response = _storeService.retrieve(new RetrieveStoreWorkforceRequest(key));
        return response.isSuccess() ? response.data : null;
    }

    protected void publish(String topic, String key, Object value) {
        _publishService.publish(topic, key, value);
    }

    protected boolean storeWorkforceData(@NotBlank String key, WorkforceData workforce) {
        SaveStoreWorkforceResponse response = _storeService.save(new SaveStoreWorkforceRequest(workforce));
        return response.isSuccess();
    }

    @Autowired
    private AppConfig _appConfig;

    @Autowired
    private IMergeService _mergeService;

    @Autowired
    private IPublishService _publishService;

    @Autowired
    private IStoreWorkforceService _storeService;

    private static final String TAG = MergeProcessorService.class.getName();
}
