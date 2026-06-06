package com.dedul.finflow.app.finflowapp.shared.exception;

public class BusinessRuleViolationException extends RuntimeException {
  public BusinessRuleViolationException(String message) {
    super(message);
  }
}
