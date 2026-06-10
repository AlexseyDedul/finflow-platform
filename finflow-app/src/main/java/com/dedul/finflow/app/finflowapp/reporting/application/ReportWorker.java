package com.dedul.finflow.app.finflowapp.reporting.application;

import com.dedul.finflow.app.finflowapp.reporting.infrastructure.persistence.ReportJobEntity;
import com.dedul.finflow.app.finflowapp.reporting.infrastructure.persistence.ReportJobRepository;

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

  @Async("applicationTaskExecutor")
  @Transactional
  public void generateMonthlyExpensesReportAsync(UUID jobId) {
    ReportJobEntity job = repository.findById(jobId)
        .orElseThrow(() -> new IllegalStateException("Report job not found: " + jobId));

    try {
      job.markRunning();

      String content = generator.generate(job.getRequestedMonth());
      String filename = "monthly-expenses-%s.csv".formatted(job.getRequestedMonth());
      String storageKey = "reports/monthly-expenses/%s/%s.csv"
          .formatted(job.getRequestedMonth(), job.getId());

      reportStorageService.upload(
          storageKey,
          CONTENT_TYPE_CSV,
          content.getBytes(StandardCharsets.UTF_8)
      );

      job.markCompleted(
          storageKey,
          CONTENT_TYPE_CSV,
          filename
      );

      log.info(
          "Monthly expense report generated and uploaded: jobId={}, storageKey={}",
          jobId,
          storageKey
      );
    } catch (Exception e) {
      job.markFailed(e.getMessage());

      log.error("Monthly expense report generation failed: jobId={}", jobId, e);
    }
  }
}