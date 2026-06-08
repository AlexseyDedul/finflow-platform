package com.dedul.finflow.app.finflowapp.shared.events;

import com.dedul.finflow.app.finflowapp.shared.events.persistence.ProcessedEventEntity;
import com.dedul.finflow.app.finflowapp.shared.events.persistence.ProcessedEventJpaRepository;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProcessedEventService {
  private final ProcessedEventJpaRepository processedEventJpaRepository;

  @Transactional(readOnly = true)
  public boolean isProcessed(UUID expenseId) {
    return processedEventJpaRepository.existsById(expenseId);
  }

  @Transactional
  public void markProcessed(UUID eventId, String eventType) {
    if (processedEventJpaRepository.existsById(eventId)) {
      return;
    }
    processedEventJpaRepository.save(new ProcessedEventEntity(eventId, eventType, Instant.now()));
  }

  @Transactional
  public boolean tryMarkProcessed(UUID eventId, String eventType) {
    try {
      if (processedEventJpaRepository.existsById(eventId)) {
        return false;
      }

      processedEventJpaRepository.saveAndFlush(
          new ProcessedEventEntity(eventId, eventType, Instant.now()));
      return true;
    } catch (DataIntegrityViolationException e) {
      return false;
    }
  }
}
