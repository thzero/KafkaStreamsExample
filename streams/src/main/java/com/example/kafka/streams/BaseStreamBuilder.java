package com.example.kafka.streams;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyDescription;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.kafka.config.IKafkaConfigRetriever;
import com.example.kafka.topology.ITopology;

public abstract class BaseStreamBuilder {
    private final Logger logger = LoggerFactory.getLogger(BaseStreamBuilder.class);

    @PostConstruct
    public void runStream() {
        Topology topology = getTopologyBuilder().defineTopology();
        TopologyDescription description = topology.describe();
        System.out.println(description.toString());
        _stream = new KafkaStreams(topology, getConfig().getProps());
        _stream.setUncaughtExceptionHandler((Thread thread, Throwable throwable) -> {
            logger.error("Uncaught exception: " + throwable.getMessage());
                // this will exit the app, forcing a restart of the container it is running in
//            closeStream();
//            Thread.currentThread().interrupt();
//            Runtime.getRuntime().exit(1);
        });

        _stream.start();

        //Graceful shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(this::closeStream));
    }

    @PreDestroy
    public void closeStream() {
        if (_stream == null)
            return;

        _stream.close(1, TimeUnit.SECONDS);
    }

    protected abstract IKafkaConfigRetriever getConfig();
    protected abstract ITopology getTopologyBuilder();

    private KafkaStreams _stream;

    private static final String TAG = BaseStreamBuilder.class.getName();
}
