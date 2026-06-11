package com.dedul.finflow.app.finflowapp.shared.outbox;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "outbox_events")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OutboxEventEntity {

  @Id private UUID id;

  @Column(name = "aggregate_id", nullable = false)
  private UUID aggregateId;

  @Column(name = "event_type", nullable = false, length = 100)
  private String eventType;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "payload", nullable = false, columnDefinition = "jsonb")
  private String payload;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 50)
  private OutboxEventStatus status;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "published_at")
  private Instant publishedAt;

  @Column(name = "retry_count")
  private Integer retryCount = 0;

  @Column(name = "last_error")
  private String lastError;

  @Column(name = "processing_started_at")
  private Instant processingStartedAt;

  @Version private Long version;

  public OutboxEventEntity(
      UUID id,
      UUID aggregateId,
      String eventType,
      String payload,
      OutboxEventStatus status,
      Instant createdAt,
      Instant publishedAt,
      Integer retryCount,
      String lastError,
      Instant processingStartedAt,
      Long version) {
    this.id = id;
    this.aggregateId = aggregateId;
    this.eventType = eventType;
    this.payload = payload;
    this.status = status;
    this.createdAt = createdAt;
    this.publishedAt = publishedAt;
    this.retryCount = retryCount == null ? 0 : retryCount;
    this.lastError = lastError;
    this.processingStartedAt = processingStartedAt;
    this.version = version;
  }

  public static OutboxEventEntity pending(UUID aggregateId, String eventType, String payload) {
    return new OutboxEventEntity(
        UUID.randomUUID(),
        aggregateId,
        eventType,
        payload,
        OutboxEventStatus.PENDING,
        Instant.now(),
        null,
        0,
        null,
        null,
        null);
  }

  public void markProcessing() {
    if (status != OutboxEventStatus.PENDING && status != OutboxEventStatus.FAILED) {
      return;
    }

    status = OutboxEventStatus.PROCESSING;
    processingStartedAt = Instant.now();
  }

  public void markPublished() {
    if (status != OutboxEventStatus.PROCESSING) {
      throw new IllegalStateException(
          "Only PROCESSING outbox event can be marked PUBLISHED. Current status: " + status);
    }

    status = OutboxEventStatus.PUBLISHED;
    publishedAt = Instant.now();
  }

  public void markFailed(String errorMessage) {
    status = OutboxEventStatus.FAILED;
    retryCount = retryCount == null ? 1 : retryCount + 1;
    lastError = errorMessage;
  }
}
