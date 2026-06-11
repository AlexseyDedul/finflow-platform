package com.dedul.finflow.app.finflowapp.reporting.infrastructure.persistence;

import com.dedul.finflow.app.finflowapp.reporting.domain.ReportJobStatus;
import com.dedul.finflow.app.finflowapp.reporting.domain.ReportType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "report_jobs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReportJobEntity {

  @Id private UUID id;

  @Enumerated(EnumType.STRING)
  @Column(name = "report_type", nullable = false, length = 100)
  private ReportType reportType;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 50)
  private ReportJobStatus status;

  @Column(name = "requested_month", nullable = false, length = 7)
  private String requestedMonth;

  @Column(name = "result_content")
  private String resultContent;

  @Column(name = "error_message")
  private String errorMessage;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "started_at")
  private Instant startedAt;

  @Column(name = "completed_at")
  private Instant completedAt;

  @Column(name = "result_storage_key", length = 500)
  private String resultStorageKey;

  @Column(name = "result_content_type", length = 100)
  private String resultContentType;

  @Column(name = "result_filename", length = 255)
  private String resultFilename;

  @Version private Long version;

  public static ReportJobEntity createMonthlyExpensesJob(String requestedMonth) {
    ReportJobEntity job = new ReportJobEntity();
    job.id = UUID.randomUUID();
    job.reportType = ReportType.MONTHLY_EXPENSES;
    job.status = ReportJobStatus.PENDING;
    job.requestedMonth = requestedMonth;
    job.createdAt = Instant.now();
    return job;
  }

  public void markRunning() {
    status = ReportJobStatus.RUNNING;
    startedAt = Instant.now();
  }

  public void markCompleted(
      String resultStorageKey, String resultContentType, String resultFilename) {
    status = ReportJobStatus.COMPLETED;
    this.resultStorageKey = resultStorageKey;
    this.resultContentType = resultContentType;
    this.resultFilename = resultFilename;
    completedAt = Instant.now();
  }

  public void markFailed(String errorMessage) {
    status = ReportJobStatus.FAILED;
    this.errorMessage = errorMessage;
    completedAt = Instant.now();
  }
}
