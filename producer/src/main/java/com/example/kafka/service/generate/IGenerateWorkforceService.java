package com.example.kafka.service.generate;

import com.example.kafka.response.generate.GenerateProducerResponse;
import com.example.kafka.service.IService;

public interface IGenerateWorkforceService extends IService {
    GenerateProducerResponse generateFromCsv() throws Exception;
}
