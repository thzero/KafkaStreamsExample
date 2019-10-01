package com.example.kafka.service;

import org.springframework.lang.NonNull;

import com.example.kafka.request.SaveExternalStoreWorkforceRequest;
import com.example.kafka.response.service.SaveExternalStoreWorkforceResponse;

public interface IExternalStoreWorkforceService {
    SaveExternalStoreWorkforceResponse saveCheckpoint(@NonNull SaveExternalStoreWorkforceRequest request);
    SaveExternalStoreWorkforceResponse saveOutput(@NonNull SaveExternalStoreWorkforceRequest request);
    SaveExternalStoreWorkforceResponse saveTransaction(@NonNull SaveExternalStoreWorkforceRequest request);
    SaveExternalStoreWorkforceResponse saveTransactionInternal(@NonNull SaveExternalStoreWorkforceRequest request);
}
