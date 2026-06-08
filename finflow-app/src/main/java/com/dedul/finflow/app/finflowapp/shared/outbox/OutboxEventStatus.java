package com.dedul.finflow.app.finflowapp.shared.outbox;

public enum OutboxEventStatus {
  PENDING,
  PROCESSING,
  PUBLISHED,
  FAILED
}
