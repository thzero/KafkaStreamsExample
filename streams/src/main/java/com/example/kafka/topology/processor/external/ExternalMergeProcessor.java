package com.example.kafka.topology.processor.external;

import javax.validation.constraints.NotBlank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.lang.NonNull;

import com.example.kafka.data.WorkforceData;
import com.example.kafka.request.RetrieveStoreWorkforceRequest;
import com.example.kafka.request.SaveStoreWorkforceRequest;
import com.example.kafka.response.store.RetrieveStoreWorkforceResponse;
import com.example.kafka.response.store.SaveStoreWorkforceResponse;
import com.example.kafka.service.merge.IMergeService;
import com.example.kafka.service.store.IStoreWorkforceService;
import com.example.kafka.topology.processor.AbstractMergeProcessor;

public class ExternalMergeProcessor extends AbstractMergeProcessor {
    private static final Logger logger = LoggerFactory.getLogger(ExternalMergeProcessor.class);

    public ExternalMergeProcessor() {}
    public ExternalMergeProcessor(@NonNull IMergeService mergeService, @NonNull IStoreWorkforceService storeService) {
        super(null, mergeService);
        _storeService = storeService;
    }

    @Override
    protected void initializeStore() {
    }

    @Override
    protected WorkforceData loadWorkforceData(@NotBlank String key) {
        RetrieveStoreWorkforceResponse response = _storeService.retrieve(new RetrieveStoreWorkforceRequest(key));
        return response.isSuccess() ? response.data : null;
    }

    @Override
    protected boolean storeWorkforceData(@NotBlank String key, WorkforceData workforce) {
        SaveStoreWorkforceResponse response = _storeService.save(new SaveStoreWorkforceRequest(workforce));
        return response.isSuccess();
    }

    private IStoreWorkforceService _storeService;

    public static final String TAG = ExternalMergeProcessor.class.getName();
}
