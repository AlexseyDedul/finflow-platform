package com.dedul.finflow.app.finflowapp.shared.outbox;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class OutboxEventEntityTest {

  @Test
  void pending_shouldCreatePendingEventWithZeroRetryCount() {
    OutboxEventEntity event =
        OutboxEventEntity.pending(
            UUID.randomUUID(),
            "ExpenseSubmittedEvent",
            """
                    {"eventId":"00000000-0000-0000-0000-000000000001"}
                    """);

    assertThat(event.getId()).isNotNull();
    assertThat(event.getStatus()).isEqualTo(OutboxEventStatus.PENDING);
    assertThat(event.getCreatedAt()).isNotNull();
    assertThat(event.getPublishedAt()).isNull();
    assertThat(event.getRetryCount()).isZero();
  }

  @Test
  void markProcessing_shouldMovePendingToProcessing() {
    OutboxEventEntity event =
        OutboxEventEntity.pending(UUID.randomUUID(), "ExpenseSubmittedEvent", "{}");

    event.markProcessing();

    assertThat(event.getStatus()).isEqualTo(OutboxEventStatus.PROCESSING);
    assertThat(event.getProcessingStartedAt()).isNotNull();
  }

  @Test
  void markPublished_shouldAllowOnlyProcessingEvent() {
    OutboxEventEntity event =
        OutboxEventEntity.pending(UUID.randomUUID(), "ExpenseSubmittedEvent", "{}");

    assertThatThrownBy(event::markPublished)
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Only PROCESSING outbox event can be marked PUBLISHED");
  }

  @Test
  void markPublished_shouldMoveProcessingToPublished() {
    OutboxEventEntity event =
        OutboxEventEntity.pending(UUID.randomUUID(), "ExpenseSubmittedEvent", "{}");

    event.markProcessing();
    event.markPublished();

    assertThat(event.getStatus()).isEqualTo(OutboxEventStatus.PUBLISHED);
    assertThat(event.getPublishedAt()).isNotNull();
  }

  @Test
  void markFailed_shouldMoveEventToFailedAndIncrementRetryCount() {
    OutboxEventEntity event =
        OutboxEventEntity.pending(UUID.randomUUID(), "ExpenseSubmittedEvent", "{}");

    event.markProcessing();
    event.markFailed("SQS unavailable");

    assertThat(event.getStatus()).isEqualTo(OutboxEventStatus.FAILED);
    assertThat(event.getRetryCount()).isEqualTo(1);
    assertThat(event.getLastError()).contains("SQS unavailable");
  }

  @Test
  void markFailed_shouldIncrementRetryCountEveryTime() {
    OutboxEventEntity event =
        OutboxEventEntity.pending(UUID.randomUUID(), "ExpenseSubmittedEvent", "{}");

    event.markFailed("first");
    event.markFailed("second");

    assertThat(event.getRetryCount()).isEqualTo(2);
    assertThat(event.getLastError()).isEqualTo("second");
  }
}
