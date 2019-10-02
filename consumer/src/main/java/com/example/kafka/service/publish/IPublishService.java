package com.example.kafka.service.publish;

import org.springframework.lang.NonNull;

import com.example.kafka.request.publish.PublishRequest;
import com.example.kafka.response.publish.PublishResponse;
import com.example.kafka.service.IService;

public interface IPublishService extends IService {
    PublishResponse publish(@NonNull PublishRequest request);
}
