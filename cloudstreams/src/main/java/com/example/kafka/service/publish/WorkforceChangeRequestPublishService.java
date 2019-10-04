package com.example.kafka.service.publish;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import com.example.kafka.listener.WorkforceChangeRequestStreams;
import com.example.kafka.request.publish.WorkforceChangeRequestPublishRequest;
import com.example.kafka.response.publish.WorkforceChangeRequestPublishResponse;
import com.example.kafka.service.BaseService;

@Service
public class WorkforceChangeRequestPublishService extends BaseService implements IWorkforceChangeRequestPublishService {
    private static final Logger logger = LoggerFactory.getLogger(WorkforceChangeRequestPublishService.class);

    public WorkforceChangeRequestPublishResponse publish(@NonNull WorkforceChangeRequestPublishRequest request) {
        WorkforceChangeRequestPublishResponse response = new WorkforceChangeRequestPublishResponse();
        try {
            MessageChannel messageChannel = null;
            if (request.topic == WorkforceChangeRequestPublishRequest.Topics.Checkpoint)
                messageChannel = workforceChangeRequestStreams.outboundChangeRequestCheckpoint();
            else if (request.topic == WorkforceChangeRequestPublishRequest.Topics.DeadLetter)
                messageChannel = workforceChangeRequestStreams.outboundChangeRequestDeadLetter();
            else if (request.topic == WorkforceChangeRequestPublishRequest.Topics.Transaction)
                messageChannel = workforceChangeRequestStreams.outboundChangeRequestTransaction();
            else if (request.topic == WorkforceChangeRequestPublishRequest.Topics.TransactionRedacted)
                messageChannel = workforceChangeRequestStreams.outboundChangeRequestTransactionRedacted();

            if (messageChannel == null)
                return error(response, "Invalid topic.");

            messageChannel.send(MessageBuilder
                    .withPayload(request.value)
                    //.setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                    .build());

            return response;
        }
        catch (Exception ex) {
            logger.error(TAG, ex);
            return error(response);
        }
    }

    @Autowired
    private WorkforceChangeRequestStreams workforceChangeRequestStreams;

    private static final String TAG = WorkforceChangeRequestPublishService.class.getName();
}
