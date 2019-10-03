package com.example.kafka.topology.advanced;

import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.example.kafka.service.merge.IMergeService;
import com.example.kafka.topology.WorkforceStreamsBuilderTopology;

@Component("advancedUpdateWorkforceTopology")
public class UpdateWorkforceTopology extends WorkforceStreamsBuilderTopology {
    private static final Logger logger = LoggerFactory.getLogger(UpdateWorkforceTopology.class);

    @Override
    protected void defineTopology(@NonNull StreamsBuilder builder) {
        // Serializing to WorkforceData as all incoming are guaranteed to be in that format as the producer is either an api that
        // validates all incoming loads, or a KStream that populates by serializing to WorkforceData.
//        // Write to the load topic, which will load back into the globalktable or ktable
        builder.stream(appConfig.changeRequestOutputTopic, Consumed.with(stringSerde, workforceSerde))
                .peek((key, value) -> {
                    logger.debug("inputStream - key: '{}' | data: {}", key, value.toString());
                })
            // Write to the load topic, which will load back into the globalktable
            .to(appConfig.loadTopic, Produced.with(stringSerde, workforceSerde));
    }

    @Autowired
    private IMergeService _mergeService;

    private static final String TAG = UpdateWorkforceTopology.class.getName();
}
