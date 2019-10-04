package com.example.kafka.service.publish;

import org.springframework.lang.NonNull;

import com.example.kafka.request.publish.WorkforceChangeRequestPublishRequest;
import com.example.kafka.response.publish.WorkforceChangeRequestPublishResponse;
import com.example.kafka.service.IService;

public interface IWorkforceChangeRequestPublishService extends IService {
    WorkforceChangeRequestPublishResponse publish(@NonNull WorkforceChangeRequestPublishRequest request);
}
