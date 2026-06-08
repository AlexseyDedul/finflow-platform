package com.dedul.finflow.app.finflowapp.shared.events.sqs;

import software.amazon.awssdk.services.sqs.model.Message;

public record SqsMessageProcessingResult(
    Message message,
    boolean success,
    Exception error
) {

  public static SqsMessageProcessingResult success(Message message) {
    return new SqsMessageProcessingResult(message, true, null);
  }

  public static SqsMessageProcessingResult failure(Message message, Exception error) {
    return new SqsMessageProcessingResult(message, false, error);
  }
}
