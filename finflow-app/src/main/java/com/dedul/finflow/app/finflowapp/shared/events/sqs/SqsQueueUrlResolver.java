package com.dedul.finflow.app.finflowapp.shared.events.sqs;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;

@Component
@RequiredArgsConstructor
public class SqsQueueUrlResolver {

  private final SqsClient sqsClient;

  public String resolve(String queueName) {
    return sqsClient
        .getQueueUrl(GetQueueUrlRequest.builder().queueName(queueName).build())
        .queueUrl();
  }
}
