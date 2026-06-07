package com.dedul.finflow.app.finflowapp.shared.exception;

public class InvalidExpenseStateException extends RuntimeException {
  public InvalidExpenseStateException(String message) {
    super(message);
  }
}
