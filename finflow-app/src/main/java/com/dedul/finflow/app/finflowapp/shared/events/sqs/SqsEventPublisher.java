package com.dedul.finflow.app.finflowapp.shared.events.sqs;

import com.dedul.finflow.app.finflowapp.shared.events.EventEnvelope;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Component
@RequiredArgsConstructor
public class SqsEventPublisher {

  private final SqsClient sqsClient;
  private final ObjectMapper objectMapper;

  public void publish(String queueUrl, EventEnvelope<?> event) {
    try {
      String body = objectMapper.writeValueAsString(event);

      sqsClient.sendMessage(
          SendMessageRequest.builder().queueUrl(queueUrl).messageBody(body).build());
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("Failed to serialize event: " + event.eventType(), e);
    }
  }

  public void publishRaw(String queueUrl, String body) {
    sqsClient.sendMessage(
        SendMessageRequest.builder().queueUrl(queueUrl).messageBody(body).build());
  }
}
