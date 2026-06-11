package com.dedul.finflow.app.finflowapp.reporting.application;

import com.dedul.finflow.app.finflowapp.reporting.infrastructure.persistence.ReportJobEntity;
import com.dedul.finflow.app.finflowapp.reporting.infrastructure.persistence.ReportJobRepository;
import com.dedul.finflow.app.finflowapp.shared.observability.BusinessMetrics;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportWorker {
  private static final String CONTENT_TYPE_CSV = "text/csv";

  private final ReportJobRepository repository;
  private final MonthlyExpenseReportGenerator generator;
  private final ReportStorageService reportStorageService;
  private final BusinessMetrics businessMetrics;

  @Async("applicationTaskExecutor")
  @Transactional
  public void generateMonthlyExpensesReportAsync(UUID jobId) {
    int claim = repository.claimPendingJob(jobId);
    if (claim == 0) {
      log.info("Report job already claimed or not pending: jobId={}", jobId);
      return;
    }

    ReportJobEntity job =
        repository
            .findById(jobId)
            .orElseThrow(() -> new IllegalStateException("Report job not found: " + jobId));

    try {
      String content = generator.generate(job.getRequestedMonth());
      String filename = "monthly-expenses-%s.csv".formatted(job.getRequestedMonth());
      String storageKey =
          "reports/monthly-expenses/%s/%s.csv".formatted(job.getRequestedMonth(), job.getId());

      reportStorageService.upload(
          storageKey, CONTENT_TYPE_CSV, content.getBytes(StandardCharsets.UTF_8));

      job.markCompleted(storageKey, CONTENT_TYPE_CSV, filename);
      businessMetrics.incrementReportsGenerated();

      log.info(
          "Monthly expense report generated and uploaded: jobId={}, storageKey={}",
          jobId,
          storageKey);
    } catch (Exception e) {
      job.markFailed(e.getMessage());
      businessMetrics.incrementReportsFailed();

      log.error("Monthly expense report generation failed: jobId={}", jobId, e);
    }
  }
}
