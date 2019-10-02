package com.example.kafka.request.communication;

import com.example.kafka.request.BaseRequest;

public class GreetingCommunicationRequest extends BaseRequest {
    public GreetingCommunicationRequest() {}
    public GreetingCommunicationRequest(String greeting) {
        this.greeting = greeting;
    }

    public String greeting;
}
