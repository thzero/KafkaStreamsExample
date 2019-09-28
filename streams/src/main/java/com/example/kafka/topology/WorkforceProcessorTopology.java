package com.example.kafka.topology;

import org.apache.kafka.streams.Topology;

import org.springframework.lang.NonNull;

public abstract class WorkforceProcessorTopology extends WorkforceBaseTopology {
    @Override
    protected Topology buildTopology() {
        Topology builder = new Topology();
        defineTopology(builder);
        return builder;
    }

    protected abstract void defineTopology(@NonNull Topology builder);
}
