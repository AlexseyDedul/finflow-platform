package com.dedul.finflow.app.finflowapp.reporting.application;

import com.dedul.finflow.app.finflowapp.reporting.api.dto.ReportJobResponse;
import com.dedul.finflow.app.finflowapp.reporting.infrastructure.persistence.ReportJobEntity;
import com.dedul.finflow.app.finflowapp.reporting.infrastructure.persistence.ReportJobRepository;
import com.dedul.finflow.app.finflowapp.shared.exception.NotFoundException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@RequiredArgsConstructor
public class ReportService {

  private final ReportJobRepository repository;
  private final ReportWorker reportWorker;
  private final ReportStorageService reportStorageService;

  @Transactional
  public ReportJobResponse createMonthlyExpensesReport(String month) {
    ReportJobEntity job = ReportJobEntity.createMonthlyExpensesJob(month);
    ReportJobEntity saved = repository.save(job);

    TransactionSynchronizationManager.registerSynchronization(
        new TransactionSynchronization() {
          @Override
          public void afterCommit() {
            reportWorker.generateMonthlyExpensesReportAsync(saved.getId());
          }
        });

    return toResponse(saved);
  }

  @Transactional(readOnly = true)
  public ReportJobResponse getJob(UUID jobId) {
    ReportJobEntity job =
        repository
            .findById(jobId)
            .orElseThrow(() -> new NotFoundException("Report job not found: " + jobId));

    return toResponse(job);
  }

  @Transactional(readOnly = true)
  public String getReportContent(UUID jobId) {
    ReportJobEntity job =
        repository
            .findById(jobId)
            .orElseThrow(() -> new NotFoundException("Report job not found: " + jobId));

    if (job.getResultContent() == null) {
      throw new IllegalStateException("Report is not ready yet: " + jobId);
    }

    return job.getResultContent();
  }

  @Transactional(readOnly = true)
  public ReportDownload getReportDownload(UUID jobId) {
    ReportJobEntity job =
        repository
            .findById(jobId)
            .orElseThrow(() -> new NotFoundException("Report job not found: " + jobId));

    if (job.getResultStorageKey() == null) {
      throw new IllegalStateException("Report is not ready yet: " + jobId);
    }

    byte[] content = reportStorageService.download(job.getResultStorageKey());

    return new ReportDownload(job.getResultFilename(), job.getResultContentType(), content);
  }

  private ReportJobResponse toResponse(ReportJobEntity job) {
    return new ReportJobResponse(
        job.getId(),
        job.getReportType(),
        job.getStatus(),
        job.getRequestedMonth(),
        job.getResultStorageKey(),
        job.getResultContentType(),
        job.getResultFilename(),
        job.getErrorMessage(),
        job.getCreatedAt(),
        job.getStartedAt(),
        job.getCompletedAt());
  }
}
