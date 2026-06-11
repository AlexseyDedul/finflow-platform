package com.dedul.finflow.app.finflowapp.shared.outbox;

import com.dedul.finflow.app.finflowapp.shared.events.sqs.SqsEventPublisher;
import com.dedul.finflow.app.finflowapp.shared.events.sqs.SqsQueueUrlResolver;
import com.dedul.finflow.app.finflowapp.shared.observability.BusinessMetrics;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxPublisher {
  private final OutboxEventRepository repository;
  private final SqsQueueUrlResolver queueUrlResolver;
  private final SqsEventPublisher sqsEventPublisher;
  private final BusinessMetrics businessMetrics;

  @Value("${app.aws.sqs.expense-submitted-queue-name}")
  private String expenseSubmittedQueueName;

  @Scheduled(fixedDelayString = "${app.outbox.polling-delay-ms:2000}")
  @Transactional
  public void publishPendingEvents() {
    List<OutboxEventEntity> events =
        repository.findByStatusOrderByCreatedAtAsc(
            OutboxEventStatus.PENDING, PageRequest.of(0, 10));

    if (events.isEmpty()) {
      return;
    }

    String queueUrl = queueUrlResolver.resolve(expenseSubmittedQueueName);

    log.info("Publishing outbox events: count={}", events.size());

    for (OutboxEventEntity candidate : events) {
      int claim = repository.claimForProcessing(candidate.getId());

      if (claim == 0) {
        log.info("Outbox event already claimed by another worker: id={}", candidate.getId());
        continue;
      }

      OutboxEventEntity event =
          repository
              .findById(candidate.getId())
              .orElseThrow(
                  () ->
                      new IllegalStateException(
                          "Claimed outbox event not found: " + candidate.getId()));

      try {
        sqsEventPublisher.publishRaw(queueUrl, event.getPayload());

        businessMetrics.incrementOutboxEventsPublished();

        log.info(
            "Published outbox event: outboxEventId={}, eventType={}, aggregateId={}",
            event.getId(),
            event.getEventType(),
            event.getAggregateId());
      } catch (Exception e) {
        String errMsg = "Error publishing outbox event: " + e.getMessage();

        event.markFailed(errMsg);
        businessMetrics.incrementOutboxEventsFailed();
        log.error(
            "Failed to publish outbox event: outboxEventId={}, eventType={}, aggregateId={}",
            event.getId(),
            event.getEventType(),
            event.getAggregateId(),
            e);
      }
    }
  }
}
