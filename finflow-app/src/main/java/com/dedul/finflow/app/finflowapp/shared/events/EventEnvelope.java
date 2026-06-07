package com.dedul.finflow.app.finflowapp.shared.events;

import java.time.Instant;
import java.util.UUID;

public record EventEnvelope<T>(UUID eventId, String eventType, Instant occurredAt, T payload) {

  public static <T> EventEnvelope<T> of(String eventType, T payload) {
    return new EventEnvelope<>(UUID.randomUUID(), eventType, Instant.now(), payload);
  }
}
