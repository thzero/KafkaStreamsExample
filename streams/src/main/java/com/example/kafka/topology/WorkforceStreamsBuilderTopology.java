package com.example.kafka.topology;

import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;

import org.springframework.lang.NonNull;

public abstract class WorkforceStreamsBuilderTopology extends WorkforceBaseTopology {
    @Override
    protected Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();
        defineTopology(builder);
        return builder.build();
    }

    protected abstract void defineTopology(@NonNull StreamsBuilder builder);
}
