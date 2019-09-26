package com.example.kafka.topology;

import org.apache.kafka.streams.Topology;

public interface ITopology {
    Topology defineTopology();
}
