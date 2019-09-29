package com.example.kafka.service;

import com.example.kafka.response.GenerateProducerResponse;

public interface IGenerateProducerService extends IService {
    GenerateProducerResponse generateFromCsv() throws Exception;
}
