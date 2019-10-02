package com.example.kafka.service.communication.simp;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

import com.example.kafka.service.ICommunicationService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.example.kafka.DateUtils;
import com.example.kafka.data.WorkforceChangeRequestData;
import com.example.kafka.service.BaseService;

@Service
public class SimpCommunicationService extends BaseService implements ICommunicationService {
    private static final Logger logger = LoggerFactory.getLogger(SimpCommunicationService.class);

    public void greet(String greeting) {
        logger.debug("Greeting for {}", greeting);

        String text = "[" + Instant.now() + "]: " + greeting;
        _messagingTemplate.convertAndSend("/topic/greetings", text);
    }

    public void transaction(String key, String value) {
        logger.debug("Transaction for {}", value);
        logger.debug("transaction received key {}: Payload: {}", key, value);

        String output = "";
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(value);
            Instant instant = Instant.now();
            ((ObjectNode)node).put("deliveredDate", DateTimeFormatter.ISO_INSTANT.format(instant));
            ((ObjectNode)node).put("deliveredTimestamp", DateUtils.toEpochSeconds(instant));
            output = mapper.writeValueAsString(node);
        }
        catch (Exception ignored) {
            output = value;
        }

        _messagingTemplate.convertAndSend("/topic/transactions", output);
    }

    public void transaction(String key, WorkforceChangeRequestData value) {
        logger.debug("Transaction for {}", value);
        logger.debug("transaction received key {}: Payload: {}", key, value);

        String output = "";
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.convertValue(value, JsonNode.class);
            Instant instant = Instant.now();
            ((ObjectNode)node).put("deliveredDate", DateTimeFormatter.ISO_INSTANT.format(instant));
            ((ObjectNode)node).put("deliveredTimestamp", DateUtils.toEpochSeconds(instant));
            output = mapper.writeValueAsString(node);
        }
        catch (Exception ignored) { }

        _messagingTemplate.convertAndSend("/topic/transactions", output);
    }

    @Autowired
    private SimpMessagingTemplate _messagingTemplate;

    // Not sure when to use one vs the other...?
    @Autowired
    private SimpMessageSendingOperations _messageOperations;
}
