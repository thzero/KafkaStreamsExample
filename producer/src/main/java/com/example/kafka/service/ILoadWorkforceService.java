package com.example.kafka.service;

import com.example.kafka.response.LoadWorkforceResponse;

public interface ILoadWorkforceService extends IService {
    LoadWorkforceResponse loadJson() throws Exception;
    LoadWorkforceResponse loadRandom() throws Exception;
}
