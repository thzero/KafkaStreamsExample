package com.example.kafka.service;

import com.example.kafka.response.LoadProducerResponse;

public interface ILoadProducerService {
    LoadProducerResponse loadRandom() throws Exception;
    LoadProducerResponse loadJson() throws Exception;
}
