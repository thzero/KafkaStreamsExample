package com.example.kafka.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import com.example.kafka.service.communication.ICommunicationService;

@Controller
public class CommunicationController {
    private static final Logger logger = LoggerFactory.getLogger(CommunicationController.class);

    @MessageMapping("/greetings")
    public void greet(String greeting) {
        _communicationService.greet(greeting);
    }

    @Autowired
    private ICommunicationService _communicationService;
}
