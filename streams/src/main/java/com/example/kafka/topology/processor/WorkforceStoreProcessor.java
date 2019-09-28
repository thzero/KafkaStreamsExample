package com.example.kafka.topology.processor;

import java.util.Objects;

import org.apache.kafka.streams.processor.AbstractProcessor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.To;
import org.apache.kafka.streams.state.KeyValueStore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.example.kafka.data.ChangeRequestData;
import com.example.kafka.data.WorkforceChangeRequestData;
import com.example.kafka.data.WorkforceData;
import com.example.kafka.response.MergeResponse;
import com.example.kafka.service.IMergeService;
import com.example.kafka.topology.advanced.KTableWorkforceTopology;

public class MergeProcessor extends AbstractProcessor<String, WorkforceChangeRequestData> {
    private static final Logger logger = LoggerFactory.getLogger(KTableWorkforceTopology.class);

    public MergeProcessor() {}
    public MergeProcessor(String storeName) {
        _storeName = storeName;
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

            logger.debug("joinedStream - joiner for request id '{}'", changeRequest.getWorkforceRequestId());

            // Lookup the workforce data
            WorkforceData workforceData = _workforceStore.get(changeRequest.getWorkforceRequestId());
            if (workforceData == null) {
                logger.warn("joinedStream - workforce data for request id '{}' was not found!", changeRequest.getWorkforceRequestId());
                // Write it to the dead-letter sink
                changeRequest.status = ChangeRequestData.Status.NotFound;
                context().forward(key, changeRequest, To.child(KeyOutputWorkforceDeadLetter));
                return;
            }

            // Merge the data
            logger.debug("joinedStream - workforce data for request id '{}' was found!", changeRequest.getWorkforceRequestId());
            logger.debug("joinedStream - before, key: '{}' | changeRequest: {} | joinedStream: {}", changeRequest.getWorkforceRequestId(), changeRequest.toString(), workforceData.toString());
            MergeResponse response = _mergeService.merge(changeRequest, workforceData);
            if (!response.isSuccess()) {
                logger.warn("joinedStream - workforce data for request id '{}' had the following error: {}", changeRequest.getWorkforceRequestId(), response.getError());
                // Write it to the dead-letter sink
                changeRequest.status = ChangeRequestData.Status.Failed;
                context().forward(key, changeRequest, To.child(KeyOutputWorkforceDeadLetter));
                context().commit();
                return;
            }

            logger.debug("joinedStream - after, key: '{}' | changeRequest: {}", response.changeRequest.getWorkforceRequestId(), response.changeRequest.toString());

            // Set the data back into the store
            _workforceStore.put(response.changeRequest.snapshot.id, response.changeRequest.snapshot);

            // Write the snapshot to the output sink
            context().forward(key, response.changeRequest.snapshot, To.child(KeyOutputWorkforce));

            // Write the transaction to the transaction sink
            changeRequest.status = ChangeRequestData.Status.Success;
            context().forward(key, response.changeRequest, To.child(KeyOutputWorkforceTransaction));

            context().commit();
        }
        catch (Exception ex) {
            logger.debug(TAG, ex);
            context().commit();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void init(ProcessorContext context) {
        _workforceStore = (KeyValueStore<String, WorkforceData>)context.getStateStore(_storeName);
        Objects.requireNonNull(_workforceStore, "State store can't be null");
    }

    private String _storeName;
    private KeyValueStore<String, WorkforceData> _workforceStore;

    @Autowired
    private IMergeService _mergeService;

    public static final String KeyOutputWorkforce = "workforce-out";
    public static final String KeyOutputWorkforceDeadLetter = "workforce-dead-letter";
    public static final String KeyOutputWorkforceTransaction = "workforce-transaction-out";

    public static final String TAG = KTableWorkforceTopology.class.getName();
}
