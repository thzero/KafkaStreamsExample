package com.example.kafka.controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.kafka.response.load.LoadWorkforceResponse;
import com.example.kafka.service.load.IExternalLoadWorkforceService;
import com.example.kafka.service.load.IProducerLoadWorkforceService;

@RestController
public class LoadController {
    private static final Logger logger = LoggerFactory.getLogger(LoadController.class);

    @GetMapping(value = "/v1/load/mongo", produces = { "application/json" })
    public LoadWorkforceResponse loadMongo() throws Exception {
        return _loadExternalService.loadJson();
    }

    @GetMapping(value = "/v1/load/processor", produces = { "application/json" })
    public LoadWorkforceResponse loadProcessor() throws Exception {
        return _loadProducerService.loadJson();
    }

    @GetMapping(value = "/v1/load/random", produces = { "application/json" })
    public LoadWorkforceResponse loadRandom() throws Exception {
        return _loadProducerService.loadRandom();
    }

    @Autowired
    private IExternalLoadWorkforceService _loadExternalService;

    @Autowired
    private IProducerLoadWorkforceService _loadProducerService;
}