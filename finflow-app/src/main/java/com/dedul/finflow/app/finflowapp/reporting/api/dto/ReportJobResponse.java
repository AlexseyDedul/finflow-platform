package com.dedul.finflow.app.finflowapp.reporting.api.dto;

import com.dedul.finflow.app.finflowapp.reporting.domain.ReportJobStatus;
import com.dedul.finflow.app.finflowapp.reporting.domain.ReportType;
import java.time.Instant;
import java.util.UUID;

public record ReportJobResponse(
    UUID id,
    ReportType reportType,
    ReportJobStatus status,
    String requestedMonth,
    String resultStorageKey,
    String resultContentType,
    String resultFilename,
    String errorMessage,
    Instant createdAt,
    Instant startedAt,
    Instant completedAt
) {}