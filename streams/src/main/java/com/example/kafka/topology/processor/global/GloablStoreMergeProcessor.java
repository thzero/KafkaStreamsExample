package com.example.kafka.topology.processor.global;

import javax.validation.constraints.NotBlank;

import org.apache.kafka.streams.processor.To;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.lang.NonNull;

import com.example.kafka.data.WorkforceData;
import com.example.kafka.service.IMergeService;
import com.example.kafka.topology.processor.AbstractMergeProcessor;

public class GloablStoreMergeProcessor extends AbstractMergeProcessor {
    private static final Logger logger = LoggerFactory.getLogger(GloablStoreMergeProcessor.class);

    public GloablStoreMergeProcessor() {}
    public GloablStoreMergeProcessor(@NonNull @NotBlank String storeName, @NonNull IMergeService mergeService) {
        super(storeName, mergeService);
    }

    protected void storeMergeResults(@NonNull @NotBlank String key, @NonNull WorkforceData workforce) {
        // Write the snapshot to the output sink
        context().forward(key, workforce, To.child(KeySinkWorkforce));
    }

    public static final String TAG = GloablStoreMergeProcessor.class.getName();
}
