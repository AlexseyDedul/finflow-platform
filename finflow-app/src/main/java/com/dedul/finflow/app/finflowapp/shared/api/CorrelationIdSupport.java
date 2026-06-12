package com.dedul.finflow.app.finflowapp.shared.api;

import org.slf4j.MDC;

public final class CorrelationIdSupport {

  private static final String CORRELATION_ID_KEY = "correlationId";

  private CorrelationIdSupport() {

  }

  public static String currentCorrelationId() {
    return MDC.get(CORRELATION_ID_KEY);
  }
}
