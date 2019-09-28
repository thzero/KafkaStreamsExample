package com.example.kafka.topology.processor;

import javax.validation.constraints.NotBlank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.lang.NonNull;

import com.example.kafka.data.WorkforceData;
import com.example.kafka.service.IMergeService;

public class MergeProcessorWithLocalStore extends GloablStoreMergeProcessor {
    private static final Logger logger = LoggerFactory.getLogger(MergeProcessorWithLocalStore.class);

    public MergeProcessorWithLocalStore() {}
    public MergeProcessorWithLocalStore(@NonNull @NotBlank String storeName, @NonNull IMergeService mergeService) {
        super(storeName, mergeService);
    }

    protected void store(@NonNull @NotBlank String key, @NonNull WorkforceData workforce) {
        // Set the data back into the store
        _workforceStore.put(key, workforce);
    }

    public static final String TAG = MergeProcessorWithLocalStore.class.getName();
}
