package com.dedul.finflow.app.finflowapp.reporting.api;

import com.dedul.finflow.app.finflowapp.reporting.api.dto.ReportJobResponse;
import com.dedul.finflow.app.finflowapp.reporting.application.ReportDownload;
import com.dedul.finflow.app.finflowapp.reporting.application.ReportService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

  private final ReportService reportService;

  @PostMapping("/monthly-expenses")
  public ReportJobResponse createMonthlyExpensesReport(@RequestParam String month) {
    return reportService.createMonthlyExpensesReport(month);
  }

  @GetMapping("/jobs/{jobId}")
  public ReportJobResponse getJob(@PathVariable UUID jobId) {
    return reportService.getJob(jobId);
  }

  @GetMapping("/jobs/{jobId}/download")
  public ResponseEntity<byte[]> download(@PathVariable UUID jobId) {
    ReportDownload download = reportService.getReportDownload(jobId);

    return ResponseEntity.ok()
        .header(
            HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=%s".formatted(download.filename()))
        .contentType(MediaType.parseMediaType("text/csv"))
        .body(download.content());
  }
}
