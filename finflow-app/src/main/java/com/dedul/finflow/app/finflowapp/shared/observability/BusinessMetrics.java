package com.dedul.finflow.app.finflowapp.shared.observability;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class BusinessMetrics {
  private final Counter sqsMessageProcessed;
  private final Counter sqsMessagesFailed;
  private final Counter outboxEventsPublished;
  private final Counter outboxEventsFailed;
  private final Counter reportsGenerated;
  private final Counter reportsFailed;

  public BusinessMetrics(MeterRegistry registry) {
    this.sqsMessageProcessed =
        Counter.builder("finflow_sqs_messages_processed_total")
            .description("Total successfully processed SQS messages")
            .register(registry);
    this.sqsMessagesFailed =
        Counter.builder("finflow_sqs_messages_failed_total")
            .description("Total failed SQS messages")
            .register(registry);

    this.outboxEventsPublished =
        Counter.builder("finflow_outbox_events_published_total")
            .description("Total successfully published outbox events")
            .register(registry);

    this.outboxEventsFailed =
        Counter.builder("finflow_outbox_events_failed_total")
            .description("Total failed outbox events")
            .register(registry);

    this.reportsGenerated =
        Counter.builder("finflow_reports_generated_total")
            .description("Total successfully generated reports")
            .register(registry);

    this.reportsFailed =
        Counter.builder("finflow_reports_failed_total")
            .description("Total failed reports")
            .register(registry);
  }

  public void incrementSqsMessagesProcessed() {
    sqsMessageProcessed.increment();
  }

  public void incrementSqsMessagesFailed() {
    sqsMessagesFailed.increment();
  }

  public void incrementOutboxEventsPublished() {
    outboxEventsPublished.increment();
  }

  public void incrementOutboxEventsFailed() {
    outboxEventsFailed.increment();
  }

  public void incrementReportsGenerated() {
    reportsGenerated.increment();
  }

  public void incrementReportsFailed() {
    reportsFailed.increment();
  }
}
