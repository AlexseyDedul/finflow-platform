package com.dedul.finflow.app.finflowapp.shared.observability;

import com.dedul.finflow.app.finflowapp.reporting.domain.ReportJobStatus;
import com.dedul.finflow.app.finflowapp.reporting.infrastructure.persistence.ReportJobRepository;
import com.dedul.finflow.app.finflowapp.shared.outbox.OutboxEventRepository;
import com.dedul.finflow.app.finflowapp.shared.outbox.OutboxEventStatus;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class OperationalGauges {
  public OperationalGauges(
      MeterRegistry registry,
      OutboxEventRepository outboxRepository,
      ReportJobRepository reportJobRepository) {
    Gauge.builder(
            "finflow_outbox_pending",
            outboxRepository,
            repository -> repository.countByStatus(OutboxEventStatus.PENDING))
        .description("Current number of pending outbox events")
        .register(registry);

    Gauge.builder(
            "finflow_outbox_failed",
            outboxRepository,
            repository -> repository.countByStatus(OutboxEventStatus.FAILED))
        .description("Current number of failed outbox events")
        .register(registry);

    Gauge.builder(
            "finflow_reports_pending",
            reportJobRepository,
            repository -> repository.countByStatus(ReportJobStatus.PENDING))
        .description("Current number of pending report jobs")
        .register(registry);

    Gauge.builder(
            "finflow_reports_running",
            reportJobRepository,
            repository -> repository.countByStatus(ReportJobStatus.RUNNING))
        .description("Current number of running report jobs")
        .register(registry);

    Gauge.builder(
            "finflow_reports_jobs_failed",
            reportJobRepository,
            repository -> repository.countByStatus(ReportJobStatus.FAILED))
        .description("Current number of failed report jobs")
        .register(registry);
  }
}
