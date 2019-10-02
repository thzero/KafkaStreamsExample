package com.example.kafka.service.communication;

import org.springframework.lang.NonNull;

import com.example.kafka.request.communication.GreetingCommunicationRequest;
import com.example.kafka.request.communication.StringTransactionCommunicationRequest;
import com.example.kafka.request.communication.WorkforceChangeRequestTransactionCommunicationRequest;
import com.example.kafka.response.communication.CommunicationResponse;
import com.example.kafka.service.IService;

public interface ICommunicationService extends IService {
    CommunicationResponse greet(@NonNull GreetingCommunicationRequest request);
    CommunicationResponse transaction(@NonNull StringTransactionCommunicationRequest request);
    CommunicationResponse transaction(@NonNull WorkforceChangeRequestTransactionCommunicationRequest request);
}
