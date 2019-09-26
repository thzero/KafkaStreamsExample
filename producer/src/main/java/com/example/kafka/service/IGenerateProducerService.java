package com.example.kafka.service;

import com.example.kafka.response.GenerateProducerResponse;

public interface IGenerateProducerService {
    GenerateProducerResponse generateFromCsv() throws Exception;
}
