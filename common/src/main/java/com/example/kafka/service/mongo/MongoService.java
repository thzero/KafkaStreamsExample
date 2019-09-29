package com.example.kafka.service.mongo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.example.kafka.data.WorkforceData;
import com.example.kafka.request.RetrieveStoreWorkforceRequest;
import com.example.kafka.request.SaveStoreWorkforceRequest;
import com.example.kafka.response.service.RetrieveStoreWorkforceResponse;
import com.example.kafka.response.service.SaveStoreWorkforceResponse;
import com.example.kafka.service.BaseService;
import com.example.kafka.service.IStoreWorkforceService;

@Component
public class MongoService extends BaseService implements IStoreWorkforceService {
    private static final Logger logger = LoggerFactory.getLogger(MongoService.class);

    @Override
    public RetrieveStoreWorkforceResponse retrieve(RetrieveStoreWorkforceRequest request) {
        RetrieveStoreWorkforceResponse response = new RetrieveStoreWorkforceResponse();
        try {
            if (StringUtils.isEmpty(request.id))
                return error(response, "Invalid id.");

            logger.debug("id: '{}'", request.id);
            response.data = _mongoTemplate.findById(request.id, WorkforceData.class, CollectionName);
            return response;
        }
        catch (Exception ex) {
            logger.error(TAG, ex);
        }

        return error(response);
    }

    @Override
    public SaveStoreWorkforceResponse save(@NonNull SaveStoreWorkforceRequest request) {
        SaveStoreWorkforceResponse response = new SaveStoreWorkforceResponse();
        try {
            if (request.data == null)
                return error(response, "Invalid data.");

            logger.debug("id: '{}' | data: {}", request.data.id, request.data.toString());
            WorkforceData data = _mongoTemplate.findById(request.data.id, WorkforceData.class, CollectionName);
            if (data == null)
                _mongoTemplate.insert(request.data, CollectionName);
            else
                _mongoTemplate.save(request.data, CollectionName);

            return response;
        }
        catch (Exception ex) {
            logger.error(TAG, ex);
        }

        return error(response);
    }

    @Autowired
    private MongoTemplate _mongoTemplate;

    private static final String CollectionName = "workforce";

    private static final String TAG = MongoService.class.getName();
}
