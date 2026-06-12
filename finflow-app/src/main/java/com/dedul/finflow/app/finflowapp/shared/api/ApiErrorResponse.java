package com.dedul.finflow.app.finflowapp.shared.api;

import java.time.Instant;
import java.util.List;

public record ApiErrorResponse(
    Instant timestamp,
    int status,
    String error,
    String message,
    String path,
    String correlationId,
    List<FieldErrorResponse> fieldErrors) {

  public static ApiErrorResponse of(
      int status, String error, String message, String path, String correlationId) {
    return new ApiErrorResponse(
        Instant.now(), status, error, message, path, correlationId, List.of());
  }

  public static ApiErrorResponse withFieldErrors(
      int status,
      String error,
      String message,
      String path,
      String correlationId,
      List<FieldErrorResponse> fieldErrors) {
    return new ApiErrorResponse(
        Instant.now(),
        status,
        error,
        message,
        path,
        correlationId,
        fieldErrors == null ? List.of() : fieldErrors);
  }
}
