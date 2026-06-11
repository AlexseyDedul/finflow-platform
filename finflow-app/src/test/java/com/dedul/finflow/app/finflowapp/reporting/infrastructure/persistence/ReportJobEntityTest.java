package com.dedul.finflow.app.finflowapp.reporting.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.dedul.finflow.app.finflowapp.reporting.domain.ReportJobStatus;
import com.dedul.finflow.app.finflowapp.reporting.domain.ReportType;
import org.junit.jupiter.api.Test;

class ReportJobEntityTest {

  @Test
  void createMonthlyExpensesJob_shouldCreatePendingJob() {
    ReportJobEntity job = ReportJobEntity.createMonthlyExpensesJob("2026-06");

    assertThat(job.getId()).isNotNull();
    assertThat(job.getReportType()).isEqualTo(ReportType.MONTHLY_EXPENSES);
    assertThat(job.getStatus()).isEqualTo(ReportJobStatus.PENDING);
    assertThat(job.getRequestedMonth()).isEqualTo("2026-06");
    assertThat(job.getCreatedAt()).isNotNull();
    assertThat(job.getStartedAt()).isNull();
    assertThat(job.getCompletedAt()).isNull();
  }

  @Test
  void markRunning_shouldMovePendingToRunning() {
    ReportJobEntity job = ReportJobEntity.createMonthlyExpensesJob("2026-06");

    job.markRunning();

    assertThat(job.getStatus()).isEqualTo(ReportJobStatus.RUNNING);
    assertThat(job.getStartedAt()).isNotNull();
  }

  @Test
  void markRunning_shouldRejectNonPendingJob() {
    ReportJobEntity job = ReportJobEntity.createMonthlyExpensesJob("2026-06");

    job.markRunning();
    job.markCompleted("reports/2026-06/report.csv", "text/csv", "report.csv");

    assertThatThrownBy(job::markRunning)
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Only PENDING report job can be started");
  }

  @Test
  void markCompleted_shouldMoveJobToCompleted() {
    ReportJobEntity job = ReportJobEntity.createMonthlyExpensesJob("2026-06");

    job.markRunning();
    job.markCompleted("reports/2026-06/report.csv", "text/csv", "report.csv");

    assertThat(job.getStatus()).isEqualTo(ReportJobStatus.COMPLETED);
    assertThat(job.getResultStorageKey()).isEqualTo("reports/2026-06/report.csv");
    assertThat(job.getResultContentType()).isEqualTo("text/csv");
    assertThat(job.getResultFilename()).isEqualTo("report.csv");
    assertThat(job.getCompletedAt()).isNotNull();
  }

  @Test
  void markFailed_shouldMoveJobToFailed() {
    ReportJobEntity job = ReportJobEntity.createMonthlyExpensesJob("2026-06");

    job.markRunning();
    job.markFailed("S3 unavailable");

    assertThat(job.getStatus()).isEqualTo(ReportJobStatus.FAILED);
    assertThat(job.getErrorMessage()).isEqualTo("S3 unavailable");
    assertThat(job.getCompletedAt()).isNotNull();
  }
}
