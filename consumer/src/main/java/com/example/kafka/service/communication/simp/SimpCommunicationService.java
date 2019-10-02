package com.example.kafka.service.communication.simp;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.example.kafka.DateUtils;
import com.example.kafka.request.communication.GreetingCommunicationRequest;
import com.example.kafka.request.communication.StringTransactionCommunicationRequest;
import com.example.kafka.request.communication.WorkforceChangeRequestTransactionCommunicationRequest;
import com.example.kafka.response.communication.CommunicationResponse;
import com.example.kafka.service.BaseService;
import com.example.kafka.service.communication.ICommunicationService;

@Service
public class SimpCommunicationService extends BaseService implements ICommunicationService {
    private static final Logger logger = LoggerFactory.getLogger(SimpCommunicationService.class);

    public CommunicationResponse greet(@NonNull GreetingCommunicationRequest request) {
        CommunicationResponse response = new CommunicationResponse();
        try {
            logger.debug("Greeting for {}", request.greeting);

            String text = "[" + Instant.now() + "]: " + request.greeting;
            _messagingTemplate.convertAndSend("/topic/greetings", text);

            return response;
        }
        catch (Exception ex) {
            logger.error(TAG, ex);
            return error(response);
        }
    }

    public CommunicationResponse transaction(@NonNull StringTransactionCommunicationRequest request) {
        CommunicationResponse response = new CommunicationResponse();
        try {
            logger.debug("Transaction for {}", request.value);
            logger.debug("transaction received key {}: Payload: {}", request.key, request.value);

            String output = "";
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode node = mapper.readTree(request.value);
                Instant instant = Instant.now();
                ((ObjectNode)node).put("deliveredDate", DateTimeFormatter.ISO_INSTANT.format(instant));
                ((ObjectNode)node).put("deliveredTimestamp", DateUtils.toEpochSeconds(instant));
                output = mapper.writeValueAsString(node);
            }
            catch (Exception ignored) {
                output = request.value;
            }

            _messagingTemplate.convertAndSend("/topic/transactions", output);

            return response;
        }
        catch (Exception ex) {
            logger.error(TAG, ex);
            return error(response);
        }
    }

    public CommunicationResponse transaction(@NonNull WorkforceChangeRequestTransactionCommunicationRequest request) {
        CommunicationResponse response = new CommunicationResponse();
        try {
            logger.debug("Transaction for {}", request.value);
            logger.debug("transaction received key {}: Payload: {}", request.key, request.value);

            String output = "";
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode node = mapper.convertValue(request.value, JsonNode.class);
                Instant instant = Instant.now();
                ((ObjectNode)node).put("deliveredDate", DateTimeFormatter.ISO_INSTANT.format(instant));
                ((ObjectNode)node).put("deliveredTimestamp", DateUtils.toEpochSeconds(instant));
                output = mapper.writeValueAsString(node);
            }
            catch (Exception ignored) { }

            _messagingTemplate.convertAndSend("/topic/transactions", output);

            return response;
        }
        catch (Exception ex) {
            logger.error(TAG, ex);
            return error(response);
        }
    }

    @Autowired
    private SimpMessagingTemplate _messagingTemplate;

    // Not sure when to use one vs the other...?
    @Autowired
    private SimpMessageSendingOperations _messageOperations;

    private static final String TAG = SimpCommunicationService.class.getName();
}
