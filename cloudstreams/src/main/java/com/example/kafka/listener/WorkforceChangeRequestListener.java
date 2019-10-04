package com.example.kafka.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.example.kafka.data.WorkforceChangeRequestData;
import com.example.kafka.request.processor.MergeProcessorRequest;
import com.example.kafka.response.processor.MergeProcessorResponse;
import com.example.kafka.service.processor.IMergeProcessorService;

@Component
public class WorkforceChangeRequestListener {
    private static final Logger logger = LoggerFactory.getLogger(WorkforceChangeRequestListener.class);

    @StreamListener(WorkforceChangeRequestStreams.INPUT_CHANGE_REQUEST)
    public void handleChangeRequest(@Payload WorkforceChangeRequestData changeRequest, @Header(KafkaHeaders.ACKNOWLEDGMENT) Acknowledgment ack) {
        logger.debug("listenAsObjectCheckpoint received key {}: Payload: {}", changeRequest.id, changeRequest);

        try {
            MergeProcessorResponse response = _processService.process(new MergeProcessorRequest(changeRequest.id, changeRequest));
            if (!response.isSuccess()) {
                if (response.getError() != null)
                    logger.error(response.getError().message);
                return;
            }

            ack.acknowledge();
        }
        catch (Exception ex) {
            logger.debug(TAG, ex);
        }
    }

    @Autowired
    private IMergeProcessorService _processService;

    private static final String TAG = WorkforceChangeRequestListener.class.getName();
}