package com.example.kafka.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.kafka.response.GenerateProducerResponse;
import com.example.kafka.service.generate.kafka.ProducerGenerateWorkforceService;

@RestController
public class GenerateController {
    private static final Logger logger = LoggerFactory.getLogger(GenerateController.class);

    @GetMapping(value = "/v1/generate", produces = { "application/json" })
    public GenerateProducerResponse generate() throws Exception {
        return  _generateProducerService.generateFromCsv();
    }

    @Autowired
    private ProducerGenerateWorkforceService _generateProducerService;
}