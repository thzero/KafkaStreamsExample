package com.example.kafka.topology.processor.global;

import javax.validation.constraints.NotBlank;

import org.apache.kafka.streams.processor.To;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.lang.NonNull;

import com.example.kafka.data.WorkforceData;
import com.example.kafka.service.IMergeService;
import com.example.kafka.topology.processor.AbstractMergeProcessor;

public class GlobalStoreMergeProcessor extends AbstractMergeProcessor {
    private static final Logger logger = LoggerFactory.getLogger(GlobalStoreMergeProcessor.class);

    public GlobalStoreMergeProcessor() {}
    public GlobalStoreMergeProcessor(@NonNull @NotBlank String storeName, @NonNull IMergeService mergeService) {
        super(storeName, mergeService);
    }

    @Override
    protected WorkforceData loadWorkforceData(@NotBlank String key) {
        return getWorkforceStore().get(key);
    }

    @Override
    protected boolean storeWorkforceData(@NonNull @NotBlank String key, @NonNull WorkforceData workforce) {
        // Write the snapshot to the output sink
        context().forward(key, workforce, To.child(KeySinkWorkforceLoad));
        return true;
    }

    public static final String TAG = GlobalStoreMergeProcessor.class.getName();
}
