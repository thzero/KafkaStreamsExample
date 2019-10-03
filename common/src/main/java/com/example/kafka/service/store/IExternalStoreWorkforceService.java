package com.example.kafka.service.store;

import org.springframework.lang.NonNull;

import com.example.kafka.request.SaveExternalStoreWorkforceRequest;
import com.example.kafka.response.store.SaveExternalStoreWorkforceResponse;

public interface IExternalStoreWorkforceService {
    SaveExternalStoreWorkforceResponse saveCheckpoint(@NonNull SaveExternalStoreWorkforceRequest request);
    SaveExternalStoreWorkforceResponse saveOutput(@NonNull SaveExternalStoreWorkforceRequest request);
    SaveExternalStoreWorkforceResponse saveTransaction(@NonNull SaveExternalStoreWorkforceRequest request);
    SaveExternalStoreWorkforceResponse saveTransactionRedacted(@NonNull SaveExternalStoreWorkforceRequest request);
}
