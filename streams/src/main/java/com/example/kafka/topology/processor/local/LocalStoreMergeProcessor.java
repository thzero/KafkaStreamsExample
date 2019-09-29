package com.example.kafka.topology.processor.local;

import javax.validation.constraints.NotBlank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.lang.NonNull;

import com.example.kafka.data.WorkforceData;
import com.example.kafka.service.IMergeService;
import com.example.kafka.topology.processor.AbstractMergeProcessor;

public class LocalStoreMergeProcessor extends AbstractMergeProcessor {
    private static final Logger logger = LoggerFactory.getLogger(LocalStoreMergeProcessor.class);

    public LocalStoreMergeProcessor() {}
    public LocalStoreMergeProcessor(@NonNull @NotBlank String storeName, @NonNull IMergeService mergeService) {
        super(storeName, mergeService);
    }

    @Override
    protected WorkforceData loadWorkforceData(@NotBlank String key) {
        return getWorkforceStore().get(key);
    }

    @Override
    protected boolean storeWorkforceData(@NotBlank String key, WorkforceData workforce) {
        // Set the data back into the store
        getWorkforceStore().put(key, workforce);
        return true;
    }

    public static final String TAG = LocalStoreMergeProcessor.class.getName();
}
