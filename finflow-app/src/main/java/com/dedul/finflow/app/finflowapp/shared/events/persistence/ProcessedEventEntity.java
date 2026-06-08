package com.dedul.finflow.app.finflowapp.shared.events.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "processed_events")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProcessedEventEntity {

  @Id
  private UUID eventId;

  @Column(nullable = false, length = 100)
  private String eventType;

  @Column(nullable = false)
  private Instant processedAt;

  public ProcessedEventEntity(UUID eventId, String eventType, Instant processedAt) {
    this.eventId = eventId;
    this.eventType = eventType;
    this.processedAt = processedAt;
  }
}
