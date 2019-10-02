package com.example.kafka.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.kafka.request.ChangeRequest;
import com.example.kafka.response.UpdateProducerResponse;
import com.example.kafka.service.changeRequest.IProducerChangeRequestWorkforceService;

@RestController
public class ChangeRequestController {
    private static final Logger logger = LoggerFactory.getLogger(ChangeRequestController.class);

    @PostMapping(value = "/v1/submit", produces = { "application/json" }, consumes = { "application/json" })
    public UpdateProducerResponse submit(@RequestBody ChangeRequest request, BindingResult result) throws Exception {
        if (result.hasErrors()) {
            logger.error("some errors");
            // TODO
            throw new Exception("errors!!!");
        }

        return _changeRequestProducerService.submit(request.changesRequests);
    }

    @Autowired
    private IProducerChangeRequestWorkforceService _changeRequestProducerService;

    private static final String TAG = ChangeRequestController.class.getName();
}