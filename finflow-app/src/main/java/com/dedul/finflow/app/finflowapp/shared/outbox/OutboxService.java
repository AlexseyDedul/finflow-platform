package com.dedul.finflow.app.finflowapp.shared.outbox;

import com.dedul.finflow.app.finflowapp.shared.events.EventEnvelope;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OutboxService {

  private final OutboxEventRepository repository;
  private final ObjectMapper objectMapper;

  @Transactional
  public void saveEvent(UUID aggregateId, String eventType, Object payload) {
    try {
      EventEnvelope<?> envelope = EventEnvelope.of(eventType, payload);
      String jsonPayload = objectMapper.writeValueAsString(envelope);

      repository.save(OutboxEventEntity.pending(aggregateId, eventType, jsonPayload));
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("Failed to serialize outbox event: " + eventType, e);
    }
  }
}
