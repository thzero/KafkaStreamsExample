package com.example.kafka.topology.processor;

import javax.validation.constraints.NotBlank;
import java.util.Objects;

import org.apache.kafka.streams.processor.AbstractProcessor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.To;
import org.apache.kafka.streams.state.KeyValueStore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import com.example.kafka.data.ChangeRequestData;
import com.example.kafka.data.ChangeTypes;
import com.example.kafka.data.WorkforceChangeRequestData;
import com.example.kafka.data.WorkforceData;
import com.example.kafka.response.MergeResponse;
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
        try {
            if (changeRequest == null) {
                context().commit();
                return;
            }
            if (StringUtils.isEmpty(changeRequest.getWorkforceRequestId())) {
                context().commit();
                return;
            }

            logger.debug("request id '{}'", changeRequest.getWorkforceRequestId());

            WorkforceData workforceData = null;
            if (changeRequest.changeTypeCd != ChangeTypes.Create) {
                // Lookup the workforce data
                workforceData = loadWorkforceData(changeRequest.getWorkforceRequestId());
                if (workforceData == null) {
                    logger.warn("workforce data for request id '{}' was not found!", changeRequest.getWorkforceRequestId());
                    // Write it to the dead-letter sink
                    changeRequest.status = ChangeRequestData.Status.NotFound;
                    context().forward(key, changeRequest, To.child(KeySinkWorkforceCheckpoint));
                    context().forward(key, changeRequest, To.child(KeySinkWorkforceDeadLetter));
                    return;
                }
                changeRequest.status = ChangeRequestData.Status.Found;
                context().forward(key, changeRequest, To.child(KeySinkWorkforceCheckpoint));
            }

            // Merge the data
            logger.debug("workforce data for request id '{}' was found!", changeRequest.getWorkforceRequestId());
            logger.debug("before, key: '{}' | changeRequest: {} | joinedStream: {}", changeRequest.getWorkforceRequestId(), changeRequest.toString(), workforceData.toString());
            MergeResponse response = _mergeService.merge(changeRequest, workforceData);
            if (!response.isSuccess()) {
                logger.warn("workforce data for request id '{}' had the following error: {}", changeRequest.getWorkforceRequestId(), response.getError());
                // Write it to the dead-letter sink
                changeRequest.status = ChangeRequestData.Status.MergeFailed;
                context().forward(key, changeRequest, To.child(KeySinkWorkforceCheckpoint));
                context().forward(key, changeRequest, To.child(KeySinkWorkforceDeadLetter));
                context().commit();
                return;
            }
            logger.debug("after, key: '{}' | changeRequest: {}", response.changeRequest.getWorkforceRequestId(), response.changeRequest.toString());
            changeRequest.status = ChangeRequestData.Status.Merged;
            context().forward(key, changeRequest, To.child(KeySinkWorkforceCheckpoint));

            storeWorkforceData(key, response.changeRequest.snapshot);
            changeRequest.status = ChangeRequestData.Status.Stored;
            context().forward(key, changeRequest, To.child(KeySinkWorkforceCheckpoint));

            // Write the transaction to the transaction internal sink
            context().forward(key, response.changeRequest, To.child(KeySinkWorkforceTransactionInternal));

            // Remove the request and snapshot; do not want to send the data when announcing a transaction
            response.changeRequest.request = null;
            response.changeRequest.snapshot = null;

            // Write the transaction to the transaction external sink
            context().forward(key, response.changeRequest, To.child(KeySinkWorkforceTransaction));

            changeRequest.status = ChangeRequestData.Status.Success;
            context().forward(key, changeRequest, To.child(KeySinkWorkforceCheckpoint));

            context().commit();
        }
        catch (Exception ex) {
            logger.debug(TAG, ex);
            context().commit();
        }
    }

    protected KeyValueStore<String, WorkforceData> getWorkforceStore() { return _workforceStore; }

    @SuppressWarnings("unchecked")
    protected void initializeStore() {
        _workforceStore = (KeyValueStore<String, WorkforceData>)context().getStateStore(_storeName);
        Objects.requireNonNull(_workforceStore, "State store can't be null");
    }

    protected abstract WorkforceData loadWorkforceData(@NonNull @NotBlank String key);

    protected abstract void storeWorkforceData(@NonNull @NotBlank String key, @NonNull WorkforceData workforce);

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
