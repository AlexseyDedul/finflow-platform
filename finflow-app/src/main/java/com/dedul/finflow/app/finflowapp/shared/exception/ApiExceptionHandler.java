package com.dedul.finflow.app.finflowapp.shared.exception;

public class ApiExceptionHandler extends RuntimeException {
  public ApiExceptionHandler(String message) {
    super(message);
  }
}
