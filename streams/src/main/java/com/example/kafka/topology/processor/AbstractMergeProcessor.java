package com.example.kafka.topology.processor;

import java.time.Instant;
import java.util.Objects;

import javax.validation.constraints.NotBlank;

import org.apache.kafka.streams.processor.AbstractProcessor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.To;
import org.apache.kafka.streams.state.KeyValueStore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import com.example.kafka.DateUtils;
import com.example.kafka.data.*;
import com.example.kafka.response.service.MergeResponse;
import com.example.kafka.service.IMergeService;

public abstract class AbstractMergeProcessor extends AbstractProcessor<String, WorkforceChangeRequestData> {
    private static final Logger logger = LoggerFactory.getLogger(AbstractMergeProcessor.class);

    public AbstractMergeProcessor() {}
    public AbstractMergeProcessor(@NonNull @NotBlank String storeName, @NonNull IMergeService mergeService) {
        _storeName = storeName;
        _mergeService = mergeService;
    }

    @Override
    public void init(ProcessorContext context) {
        super.init(context);
        initializeStore();
    }

    @Override
    public void process(String key, WorkforceChangeRequestData changeRequest) {
        if (StringUtils.isEmpty(key)) {
            // TODO
            // Should we throw an exception here?  That would cause an unhandled exception, which is caught the handler, and
            // shutdown the app otherwise forwarding will auto-commit.
            context().forward(key, changeRequest, To.child(KeySinkWorkforceDeadLetter));
            context().commit();
            return;
        }
        if (changeRequest == null) {
            // TODO
            // Should we throw an exception here?  That would cause an unhandled exception, which is caught the handler, and
            // shutdown the app otherwise forwarding will auto-commit.
            context().forward(key, changeRequest, To.child(KeySinkWorkforceDeadLetter));
            context().commit();
            return;
        }
        if (StringUtils.isEmpty(changeRequest.getWorkforceRequestId())) {
            // TODO
            // Should we throw an exception here?  That would cause an unhandled exception, which is caught the handler, and
            // shutdown the app otherwise forwarding will auto-commit.
            context().forward(key, changeRequest, To.child(KeySinkWorkforceDeadLetter));
            context().commit();
            return;
        }

        try {
            logger.debug("request id '{}'", changeRequest.getWorkforceRequestId());

            WorkforceData workforceData = null;
            if (changeRequest.changeTypeCd != ChangeTypes.Create) {
                // Lookup the workforce data
                workforceData = loadWorkforceData(changeRequest.getWorkforceRequestId());
                if (workforceData == null) {
                    logger.warn("workforce data for request id '{}' was not found!", changeRequest.getWorkforceRequestId());
                    // TODO
                    // Should we throw an exception here?  That would cause an unhandled exception, which is caught the handler, and
                    // shutdown the app otherwise forwarding will auto-commit.
                    // Write it to the dead-letter sink
                    setProcessedStatus(changeRequest, ChangeRequestData.ProcessStatus.NotFound);
                    context().forward(key, changeRequest, To.child(KeySinkWorkforceCheckpoint));
                    context().forward(key, changeRequest, To.child(KeySinkWorkforceDeadLetter));
                    return;
                }
                setProcessedStatus(changeRequest, ChangeRequestData.ProcessStatus.Found);
                context().forward(key, changeRequest, To.child(KeySinkWorkforceCheckpoint));
            }

            logger.debug("workforce data for request id '{}' was found!", changeRequest.getWorkforceRequestId());

            // Merge the data
            logger.debug("before, key: '{}' | changeRequest: {} | joinedStream: {}", changeRequest.getWorkforceRequestId(), changeRequest.toString(), workforceData.toString());
            MergeResponse mergeResponse = getMergeService().merge(changeRequest, workforceData);
            if (!mergeResponse.isSuccess()) {
                logger.warn("workforce data for request id '{}' had the following error: {}", changeRequest.getWorkforceRequestId(), mergeResponse.getError());
                // TODO
                // Should we throw an exception here?  That would cause an unhandled exception, which is caught the handler, and
                // shutdown the app otherwise forwarding will auto-commit.
                // Write it to the dead-letter sink
                setProcessedStatus(changeRequest, ChangeRequestData.ProcessStatus.MergeFailed);
                context().forward(key, changeRequest, To.child(KeySinkWorkforceCheckpoint));
                context().forward(key, changeRequest, To.child(KeySinkWorkforceDeadLetter));
                context().commit();
                return;
            }
            logger.debug("after, key: '{}' | changeRequest: {}", mergeResponse.changeRequest.getWorkforceRequestId(), mergeResponse.changeRequest.toString());
            setProcessedStatus(changeRequest, ChangeRequestData.ProcessStatus.Merged);
            context().forward(key, changeRequest, To.child(KeySinkWorkforceCheckpoint));

            boolean result = storeWorkforceData(key, mergeResponse.changeRequest.snapshot);
            if (!result) {
                // TODO
                // Should we throw an exception here?  That would cause an unhandled exception, which is caught the handler, and
                // shutdown the app otherwise forwarding will auto-commit.
                setProcessedStatus(changeRequest, ChangeRequestData.ProcessStatus.StoreFailed);
                context().forward(key, changeRequest, To.child(KeySinkWorkforceCheckpoint));
                return;
            }
            setProcessedStatus(changeRequest, ChangeRequestData.ProcessStatus.Stored);
            context().forward(key, changeRequest, To.child(KeySinkWorkforceCheckpoint));

            // Write the transaction to the transaction internal sink
            context().forward(key, mergeResponse.changeRequest, To.child(KeySinkWorkforceTransactionInternal));

            // Remove the request and snapshot; do not want to send the data when announcing a transaction
            mergeResponse.changeRequest.request = null;
            mergeResponse.changeRequest.snapshot = null;

            // Write the transaction to the transaction external sink
            context().forward(key, mergeResponse.changeRequest, To.child(KeySinkWorkforceTransaction));

            setProcessedStatus(changeRequest, ChangeRequestData.ProcessStatus.Success);
            context().forward(key, changeRequest, To.child(KeySinkWorkforceCheckpoint));

            context().commit();
        }
        catch (Exception ex) {
            logger.debug(TAG, ex);
            // TODO
            // Should we throw an exception here?  That would cause an unhandled exception, which is caught the handler, and
            // shutdown the app otherwise forwarding will auto-commit.
            setProcessedStatus(changeRequest, ChangeRequestData.ProcessStatus.Failed);
            context().forward(key, changeRequest, To.child(KeySinkWorkforceCheckpoint));
            context().forward(key, changeRequest, To.child(KeySinkWorkforceDeadLetter));
            context().commit();
        }
    }

    protected void setProcessedStatus(@NonNull WorkforceChangeRequestData changeRequest, ChangeRequestData.ProcessStatus status) {
        Instant instant = Instant.now();
        changeRequest.processDate = DateUtils.toDate(instant);
        changeRequest.processTimestamp = DateUtils.toEpochSeconds(instant);
        changeRequest.processStatus = status;
    }

    protected KeyValueStore<String, WorkforceData> getWorkforceStore() { return _workforceStore; }

    protected IMergeService getMergeService() {
        return _mergeService;
    }

    @SuppressWarnings("unchecked")
    protected void initializeStore() {
        _workforceStore = (KeyValueStore<String, WorkforceData>)context().getStateStore(_storeName);
        Objects.requireNonNull(_workforceStore, "State store can't be null");
    }

    protected abstract WorkforceData loadWorkforceData(@NonNull @NotBlank String key);

    protected abstract boolean storeWorkforceData(@NonNull @NotBlank String key, @NonNull WorkforceData workforce);

    private String _storeName;
    private KeyValueStore<String, WorkforceData> _workforceStore;

    private IMergeService _mergeService;

    public static final String KeySinkWorkforceCheckpoint = "sink-workforce-checkpoint";
    public static final String KeySinkWorkforceDeadLetter = "sink-workforce-dead-letter";
    public static final String KeySinkWorkforceLoad = "sink-workforce-load";
    public static final String KeySinkWorkforceTransaction = "sink-workforce-transaction";
    public static final String KeySinkWorkforceTransactionInternal = "sink-workforce-transaction-internal";

    public static final String TAG = AbstractMergeProcessor.class.getName();
}
