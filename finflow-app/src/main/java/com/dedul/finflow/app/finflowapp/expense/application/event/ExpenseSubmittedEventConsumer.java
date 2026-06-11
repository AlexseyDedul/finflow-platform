package com.dedul.finflow.app.finflowapp.expense.application.event;

import com.dedul.finflow.app.finflowapp.shared.events.EventEnvelope;
import com.dedul.finflow.app.finflowapp.shared.events.ProcessedEventService;
import com.dedul.finflow.app.finflowapp.shared.events.sqs.SqsMessageProcessingResult;
import com.dedul.finflow.app.finflowapp.shared.events.sqs.SqsQueueUrlResolver;
import com.dedul.finflow.app.finflowapp.shared.observability.BusinessMetrics;
import com.dedul.finflow.app.finflowapp.workflow.application.WorkflowService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExpenseSubmittedEventConsumer {

  private final SqsClient sqsClient;
  private final SqsQueueUrlResolver queueUrlResolver;
  private final WorkflowService workflowService;
  private final ProcessedEventService processedEventService;
  private final ObjectMapper objectMapper;
  private final BusinessMetrics businessMetrics;

  @Qualifier("sqsMessageProcessingExecutor") private final Executor sqsMessageProcessingExecutor;

  @Value("${app.aws.sqs.expense-submitted-queue-name}")
  private String queueName;

  @Scheduled(fixedDelayString = "${app.aws.sqs.polling-delay-ms:5000}")
  public void poll() {
    String queueUrl = queueUrlResolver.resolve(queueName);

    var messages =
        sqsClient
            .receiveMessage(
                ReceiveMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .maxNumberOfMessages(10)
                    .waitTimeSeconds(2)
                    .build())
            .messages();

    if (messages.isEmpty()) {
      return;
    }

    log.info("Received SQS messages: count={}", messages.size());

    List<CompletableFuture<SqsMessageProcessingResult>> futures =
        messages.stream()
            .map(
                message ->
                    CompletableFuture.supplyAsync(
                        () -> processMessage(message), sqsMessageProcessingExecutor))
            .toList();

    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

    List<SqsMessageProcessingResult> results =
        futures.stream().map(CompletableFuture::join).toList();

    long successCount = results.stream().filter(SqsMessageProcessingResult::success).count();
    long failureCount = results.size() - successCount;

    log.info(
        "Finished SQS batch processing: total={}, success={}, failed={}",
        results.size(),
        successCount,
        failureCount);

    results.stream()
        .filter(SqsMessageProcessingResult::success)
        .map(SqsMessageProcessingResult::message)
        .forEach(message -> deleteMessage(queueUrl, message));
  }

  private SqsMessageProcessingResult processMessage(Message message) {
    try {
      handle(message);
      businessMetrics.incrementSqsMessagesProcessed();
      return SqsMessageProcessingResult.success(message);
    } catch (Exception e) {
      businessMetrics.incrementSqsMessagesFailed();
      log.error("Failed to process SQS message: messageId={}", message.messageId(), e);
      return SqsMessageProcessingResult.failure(message, e);
    }
  }

  private void deleteMessage(String queueUrl, Message message) {
    sqsClient.deleteMessage(
        DeleteMessageRequest.builder()
            .queueUrl(queueUrl)
            .receiptHandle(message.receiptHandle())
            .build());
  }

  private void handle(Message message) throws Exception {
    EventEnvelope<JsonNode> envelope =
        objectMapper.readValue(message.body(), new TypeReference<EventEnvelope<JsonNode>>() {});

    if (processedEventService.isProcessed(envelope.eventId())) {
      log.info(
          "SQS event already processed, skipping: eventId={}, eventType={}",
          envelope.eventId(),
          envelope.eventType());
      return;
    }

    ExpenseSubmittedEvent event =
        objectMapper.treeToValue(envelope.payload(), ExpenseSubmittedEvent.class);

    workflowService.startExpenseApproval(event);
    boolean marked =
        processedEventService.tryMarkProcessed(envelope.eventId(), envelope.eventType());

    if (!marked) {
      log.info(
          "SQS event was already marked processed concurrently: eventId={}, eventType={}",
          envelope.eventId(),
          envelope.eventType());
    }
  }
}
