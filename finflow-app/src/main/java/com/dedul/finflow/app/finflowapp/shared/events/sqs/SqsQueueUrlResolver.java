package com.dedul.finflow.app.finflowapp.shared.events.sqs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;

@Slf4j
@Component
@RequiredArgsConstructor
public class SqsQueueUrlResolver {

  private final SqsClient sqsClient;

  public String resolve(String queueName) {
    log.info("Resolving SQS queue URL: queueName={}", queueName);
    return sqsClient
        .getQueueUrl(GetQueueUrlRequest.builder().queueName(queueName).build())
        .queueUrl();
  }
}
