package com.example.kafka.topology.processor.external;

import javax.validation.constraints.NotBlank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.lang.NonNull;

import com.example.kafka.data.WorkforceData;
import com.example.kafka.service.IMergeService;
import com.example.kafka.topology.processor.AbstractMergeProcessor;

public class ExternalMergeProcessor extends AbstractMergeProcessor {
    private static final Logger logger = LoggerFactory.getLogger(ExternalMergeProcessor.class);

    public ExternalMergeProcessor() {}
    public ExternalMergeProcessor(@NonNull @NotBlank String storeName, @NonNull IMergeService mergeService) {
        super(storeName, mergeService);
    }

    @Override
    protected void initializeStore() {
    }

    @Override
    protected WorkforceData loadWorkforceData(@NotBlank String key) {
        // TODO
        return null;
    }

    @Override
    protected void storeWorkforceData(@NotBlank String key, WorkforceData workforce) {
        // TODO
    }

    public static final String TAG = ExternalMergeProcessor.class.getName();
}
