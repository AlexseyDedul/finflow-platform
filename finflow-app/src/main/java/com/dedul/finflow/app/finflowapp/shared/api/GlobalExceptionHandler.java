package com.dedul.finflow.app.finflowapp.shared.api;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiErrorResponse> handleValidationException(
      MethodArgumentNotValidException exception, HttpServletRequest request) {

    List<FieldErrorResponse> fieldErrors =
        exception.getBindingResult().getFieldErrors().stream()
            .map(
                fieldError ->
                    new FieldErrorResponse(
                        fieldError.getField(),
                        fieldError.getDefaultMessage(),
                        fieldError.getRejectedValue()))
            .toList();

    ApiErrorResponse body =
        ApiErrorResponse.withFieldErrors(
            HttpStatus.BAD_REQUEST.value(),
            "VALIDATION_ERROR",
            "Request validation failed",
            request.getRequestURI(),
            CorrelationIdSupport.currentCorrelationId(),
            fieldErrors);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ApiErrorResponse> handleConstraintViolationException(
      ConstraintViolationException exception, HttpServletRequest request) {

    List<FieldErrorResponse> fieldErrors =
        exception.getConstraintViolations().stream()
            .map(
                violation ->
                    new FieldErrorResponse(
                        violation.getPropertyPath().toString(),
                        violation.getMessage(),
                        violation.getInvalidValue()))
            .toList();

    ApiErrorResponse body =
        ApiErrorResponse.withFieldErrors(
            HttpStatus.BAD_REQUEST.value(),
            "VALIDATION_ERROR",
            "Request validation failed",
            request.getRequestURI(),
            CorrelationIdSupport.currentCorrelationId(),
            fieldErrors);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
  }

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<ApiErrorResponse> handleResponseStatusException(
      ResponseStatusException exception, HttpServletRequest request) {

    int status = exception.getStatusCode().value();

    ApiErrorResponse body =
        ApiErrorResponse.of(
            status,
            exception.getStatusCode().toString(),
            exception.getReason() == null ? "Request failed" : exception.getReason(),
            request.getRequestURI(),
            CorrelationIdSupport.currentCorrelationId());

    return ResponseEntity.status(exception.getStatusCode()).body(body);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ApiErrorResponse> handleAccessDeniedException(
      AccessDeniedException exception, HttpServletRequest request) {

    ApiErrorResponse body =
        ApiErrorResponse.of(
            HttpStatus.FORBIDDEN.value(),
            "FORBIDDEN",
            "Access denied",
            request.getRequestURI(),
            CorrelationIdSupport.currentCorrelationId());

    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(
      IllegalArgumentException exception, HttpServletRequest request) {

    ApiErrorResponse body =
        ApiErrorResponse.of(
            HttpStatus.BAD_REQUEST.value(),
            "BAD_REQUEST",
            exception.getMessage(),
            request.getRequestURI(),
            CorrelationIdSupport.currentCorrelationId());

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<ApiErrorResponse> handleIllegalStateException(
      IllegalStateException exception, HttpServletRequest request) {

    ApiErrorResponse body =
        ApiErrorResponse.of(
            HttpStatus.CONFLICT.value(),
            "CONFLICT",
            exception.getMessage(),
            request.getRequestURI(),
            CorrelationIdSupport.currentCorrelationId());

    return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiErrorResponse> handleUnexpectedException(
      Exception exception, HttpServletRequest request) {

    ApiErrorResponse body =
        ApiErrorResponse.of(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "INTERNAL_SERVER_ERROR",
            "Unexpected server error",
            request.getRequestURI(),
            CorrelationIdSupport.currentCorrelationId());

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
  }

  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<ApiErrorResponse> handleNoResourceFoundException(
      NoResourceFoundException exception, HttpServletRequest request) {

    ApiErrorResponse body =
        ApiErrorResponse.of(
            HttpStatus.NOT_FOUND.value(),
            "NOT_FOUND",
            "Resource not found",
            request.getRequestURI(),
            CorrelationIdSupport.currentCorrelationId());

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
  }
}
