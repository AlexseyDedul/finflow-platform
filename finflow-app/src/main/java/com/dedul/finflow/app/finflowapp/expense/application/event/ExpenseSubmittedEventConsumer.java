package com.dedul.finflow.app.finflowapp.expense.application.event;

import com.dedul.finflow.app.finflowapp.shared.events.sqs.SqsQueueUrlResolver;
import com.dedul.finflow.app.finflowapp.workflow.application.WorkflowService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExpenseSubmittedEventConsumer {

  private final SqsClient sqsClient;
  private final SqsQueueUrlResolver queueUrlResolver;
  private final WorkflowService workflowService;
  private final ObjectMapper objectMapper;

  @Value("${app.aws.sqs.expense-submitted-queue-name}")
  private String queueName;

  @Scheduled(fixedDelayString = "${app.aws.sqs.polling-delay-ms:5000}")
  public void poll() {
    String queueUrl = queueUrlResolver.resolve(queueName);

    var response =
        sqsClient.receiveMessage(
            ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(10)
                .waitTimeSeconds(2)
                .build());

    for (var message : response.messages()) {
      try {
        JsonNode root = objectMapper.readTree(message.body());
        JsonNode payloadNode = root.get("payload");
        ExpenseSubmittedEvent event =
            objectMapper.treeToValue(payloadNode, ExpenseSubmittedEvent.class);
        log.info(
            "Received SQS event: eventId={}, eventType={}, payload={}",
            root.get("eventId").asText(),
            root.get("eventType").asText(),
            root.get("payload"));

        workflowService.createExpenseApproval(event);

        sqsClient.deleteMessage(
            DeleteMessageRequest.builder()
                .queueUrl(queueUrl)
                .receiptHandle(message.receiptHandle())
                .build());
      } catch (Exception e) {
        log.error("Failed to process SQS message: messageId={}", message.messageId(), e);
      }
    }
  }
}
