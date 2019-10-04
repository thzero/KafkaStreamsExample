package com.example.kafka.listener;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface WorkforceChangeRequestStreams {
    String INPUT_CHANGE_REQUEST = "workforce_change_request";
    String OUTPUT_CHANGE_REQUEST_CHECKPOINT = "workforce_change_request_checkpoint";
    String OUTPUT_CHANGE_REQUEST_DEAD_LETTER = "workforce_change_request_dead_letter";
    String OUTPUT_CHANGE_REQUEST_TRANSACTION = "workforce_change_request_transaction";
    String OUTPUT_CHANGE_REQUEST_TRANSACTION_REDACTED = "workforce_change_request_transaction_redacted";
    @Input(INPUT_CHANGE_REQUEST)
    SubscribableChannel inboundChangeRequest();
    @Output(OUTPUT_CHANGE_REQUEST_CHECKPOINT)
    MessageChannel outboundChangeRequestCheckpoint();
    @Output(OUTPUT_CHANGE_REQUEST_DEAD_LETTER)
    MessageChannel outboundChangeRequestDeadLetter();
    @Output(OUTPUT_CHANGE_REQUEST_TRANSACTION)
    MessageChannel outboundChangeRequestTransaction();
    @Output(OUTPUT_CHANGE_REQUEST_TRANSACTION_REDACTED)
    MessageChannel outboundChangeRequestTransactionRedacted();
}