package com.example.kafka.service.load;

import com.example.kafka.response.LoadWorkforceResponse;
import com.example.kafka.service.IService;

public interface ILoadWorkforceService extends IService {
    LoadWorkforceResponse loadJson() throws Exception;
    LoadWorkforceResponse loadRandom() throws Exception;
}
