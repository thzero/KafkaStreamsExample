package com.example.kafka.service.publish.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.example.kafka.request.publish.PublishRequest;
import com.example.kafka.response.publish.PublishResponse;
import com.example.kafka.service.BaseService;
import com.example.kafka.service.publish.IPublishService;

@Service
public class PublishService extends BaseService implements IPublishService {
    private static final Logger logger = LoggerFactory.getLogger(PublishService.class);

    public PublishResponse publish(@NonNull PublishRequest request) {
        PublishResponse response = new PublishResponse();
        try {
            _kafkaTemplate.send(request.topic, request.key, request.value);

            return response;
        }
        catch (Exception ex) {
            logger.error(TAG, ex);
            return error(response);
        }
    }

    @Autowired
    private KafkaTemplate<String, Object> _kafkaTemplate;

    private static final String TAG = PublishService.class.getName();
}
