package com.dedul.finflow.app.finflowapp.reporting.application;

public record ReportDownload(
    String filename,
    String contentType,
    byte[] content
) {}