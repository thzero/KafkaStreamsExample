package com.example.kafka.service.processor.merge;

import java.time.Instant;

import javax.validation.constraints.NotBlank;

import com.example.kafka.response.processor.MergeProcessorResponse;
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
import com.example.kafka.request.processor.MergeProcessorRequest;
import com.example.kafka.request.RetrieveStoreWorkforceRequest;
import com.example.kafka.request.SaveStoreWorkforceRequest;
import com.example.kafka.request.publish.WorkforceChangeRequestPublishRequest;
import com.example.kafka.response.merge.MergeResponse;
import com.example.kafka.response.store.RetrieveStoreWorkforceResponse;
import com.example.kafka.response.store.SaveStoreWorkforceResponse;
import com.example.kafka.service.BaseService;
import com.example.kafka.service.merge.IMergeService;
import com.example.kafka.service.publish.IWorkforceChangeRequestPublishService;
import com.example.kafka.service.processor.IMergeProcessorService;
import com.example.kafka.service.store.IStoreWorkforceService;

@Service
public class MergeProcessorService extends BaseService implements IMergeProcessorService {
    private static final Logger logger = LoggerFactory.getLogger(MergeProcessorService.class);

    public MergeProcessorResponse process(@NonNull MergeProcessorRequest request) {
        MergeProcessorResponse response = new MergeProcessorResponse();
        try {
            if (StringUtils.isEmpty(request.key))
                return error(response);
            if (request.changeRequest == null)
                return error(response);

            if (StringUtils.isEmpty(request.changeRequest.getWorkforceRequestId()))
                return error(response);

            logger.debug("request id '{}'", request.changeRequest.getWorkforceRequestId());

            WorkforceData workforceData = null;
            if (request.changeRequest.changeTypeCd != ChangeTypes.Create) {
                // Lookup the workforce data
                workforceData = loadWorkforceData(request.changeRequest.getWorkforceRequestId());
                if (workforceData == null) {
                    logger.warn("workforce data for request id '{}' was not found!", request.changeRequest.getWorkforceRequestId());
                    return error(response);
                }
                setProcessedStatus(request.changeRequest, ChangeRequestData.ProcessStatus.Found);
                publish(_appConfig.changeRequestCheckpointTopic, request.key, request.changeRequest);
            }

            logger.debug("workforce data for request id '{}' was found!", request.changeRequest.getWorkforceRequestId());

            // Merge the data
            logger.debug("before, key: '{}' | changeRequest: {} | joinedStream: {}", request.changeRequest.getWorkforceRequestId(), request.changeRequest.toString(), workforceData.toString());
            MergeResponse mergeResponse = getMergeService().merge(request.changeRequest, workforceData);
            if (!mergeResponse.isSuccess()) {
                logger.warn("workforce data for request id '{}' had the following error: {}", request.changeRequest.getWorkforceRequestId(), response.getError());
                // TODO
                // Should we throw an exception here?  That would cause an unhandled exception, which is caught the handler, and
                // shutdown the app otherwise forwarding will auto-commit.
                // Write it to the dead-letter sink
                setProcessedStatus(request.changeRequest, ChangeRequestData.ProcessStatus.MergeFailed);
                publish(_appConfig.changeRequestCheckpointTopic, request.key, request.changeRequest);
                publish(_appConfig.changeRequestDeadLetterTopic, request.key, request.changeRequest);
                return error(response);
            }
            logger.debug("after, key: '{}' | changeRequest: {}", mergeResponse.changeRequest.getWorkforceRequestId(), mergeResponse.changeRequest.toString());
            setProcessedStatus(request.changeRequest, ChangeRequestData.ProcessStatus.Merged);
            publish(_appConfig.changeRequestCheckpointTopic, request.key, request.changeRequest);

            boolean result = storeWorkforceData(request.key, mergeResponse.changeRequest.snapshot);
            if (!result) {
                // TODO
                // Should we throw an exception here?  That would cause an unhandled exception, which is caught the handler, and
                // shutdown the app otherwise forwarding will auto-commit.
                setProcessedStatus(request.changeRequest, ChangeRequestData.ProcessStatus.StoreFailed);
                publish(_appConfig.changeRequestCheckpointTopic, request.key, request.changeRequest);
                return error(response);
            }
            setProcessedStatus(request.changeRequest, ChangeRequestData.ProcessStatus.Stored);
            publish(_appConfig.changeRequestCheckpointTopic, request.key, request.changeRequest);

            // Write the transaction to the transaction internal sink
            publish(_appConfig.changeRequestTransactionInternalTopic, request.key, request.changeRequest);

            // Remove the request and snapshot; do not want to send the data when announcing a transaction
            mergeResponse.changeRequest.request = null;
            mergeResponse.changeRequest.snapshot = null;

            // Write the transaction to the transaction external sink
            publish(_appConfig.changeRequestTransactionTopic, request.key, mergeResponse.changeRequest);

            setProcessedStatus(request.changeRequest, ChangeRequestData.ProcessStatus.Success);
            publish(_appConfig.changeRequestCheckpointTopic, request.key, request.changeRequest);

            return response;
        }
        catch (Exception ex) {
            logger.debug(TAG, ex);
            try {
                setProcessedStatus(request.changeRequest, ChangeRequestData.ProcessStatus.Failed);
                publish(_appConfig.changeRequestCheckpointTopic, request.key, request.changeRequest);
                publish(_appConfig.changeRequestDeadLetterTopic, request.key, request.changeRequest);
            }
            catch (Exception ex2) {
                logger.debug(TAG, ex);
            }
        }

        return error(response);
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

    protected void publish(String topic, String key, WorkforceChangeRequestData value) {
        _publishService.publish(new WorkforceChangeRequestPublishRequest(topic, key, value));
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
    private IWorkforceChangeRequestPublishService _publishService;

    @Autowired
    private IStoreWorkforceService _storeService;

    private static final String TAG = MergeProcessorService.class.getName();
}
