package com.example.kafka.service.mongo;

import javax.validation.constraints.NotBlank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.example.kafka.data.WorkforceChangeRequestData;
import com.example.kafka.request.SaveExternalStoreWorkforceRequest;
import com.example.kafka.response.store.SaveExternalStoreWorkforceResponse;
import com.example.kafka.service.BaseService;
import com.example.kafka.service.store.IExternalStoreWorkforceService;

@Component
public class ExternalStoreWorkforceService extends BaseService implements IExternalStoreWorkforceService {
    private static final Logger logger = LoggerFactory.getLogger(ExternalStoreWorkforceService.class);

    @Override
    public SaveExternalStoreWorkforceResponse saveCheckpoint(@NonNull SaveExternalStoreWorkforceRequest request) {
        return save(request, CollectionNameCheckpoint);
    }

    @Override
    public SaveExternalStoreWorkforceResponse saveOutput(@NonNull SaveExternalStoreWorkforceRequest request) {
        return save(request, CollectionNameOutput);
    }

    @Override
    public SaveExternalStoreWorkforceResponse saveTransaction(@NonNull SaveExternalStoreWorkforceRequest request) {
        return save(request, CollectionNameTransaction);
    }

    @Override
    public SaveExternalStoreWorkforceResponse saveTransactionInternal(@NonNull SaveExternalStoreWorkforceRequest request) {
        return save(request, CollectionNameTransactionInternal);
    }

    private SaveExternalStoreWorkforceResponse save(@NonNull SaveExternalStoreWorkforceRequest request, @NonNull @NotBlank String collectionName) {
        SaveExternalStoreWorkforceResponse response = new SaveExternalStoreWorkforceResponse();
        try {
            if (request.data == null)
                return error(response, "Invalid data.");

            logger.debug("id: '{}' | data: {}", request.data.id, request.data.toString());
            WorkforceChangeRequestData data = _mongoTemplate.findById(request.data.id, WorkforceChangeRequestData.class, collectionName);
            if (data == null)
                _mongoTemplate.insert(request.data, collectionName);
            else
                _mongoTemplate.save(request.data, collectionName);

            return response;
        }
        catch (Exception ex) {
            logger.error(TAG, ex);
        }

        return error(response);
    }

    @Autowired
    private MongoTemplate _mongoTemplate;

    private static final String CollectionNameCheckpoint = "changeRequestCheckpoint";
    private static final String CollectionNameOutput = "changeRequestOutput";
    private static final String CollectionNameTransaction = "changeRequestTransaction";
    private static final String CollectionNameTransactionInternal = "changeRequestTransactionInternal";

    private static final String TAG = ExternalStoreWorkforceService.class.getName();
}
