package com.example.kafka.service;

import org.springframework.lang.NonNull;

import com.example.kafka.request.RetrieveStoreWorkforceRequest;
import com.example.kafka.request.SaveStoreWorkforceRequest;
import com.example.kafka.response.service.RetrieveStoreWorkforceResponse;
import com.example.kafka.response.service.SaveStoreWorkforceResponse;

public interface IStoreWorkforceService {
    RetrieveStoreWorkforceResponse retrieve(@NonNull RetrieveStoreWorkforceRequest request);
    SaveStoreWorkforceResponse save(@NonNull SaveStoreWorkforceRequest request);
}
