package com.dedul.finflow.app.finflowapp.shared.outbox;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class OutboxEventRepositoryTest {
  @Autowired
  private OutboxEventRepository repository;

  @Test
  void claimForProcessing_shouldMovePendingEventToProcessing() {
    OutboxEventEntity event =
        repository.saveAndFlush(
            OutboxEventEntity.pending(UUID.randomUUID(), "ExpenseSubmittedEvent", "{}"));
    int claimed = repository.claimForProcessing(event.getId());
    assertThat(claimed).isEqualTo(1);
    OutboxEventEntity reloaded = repository.findById(event.getId()).orElseThrow();
    assertThat(reloaded.getStatus()).isEqualTo(OutboxEventStatus.PROCESSING);
    assertThat(reloaded.getProcessingStartedAt()).isNotNull();
  }

  @Test
  void claimForProcessing_shouldNotClaimAlreadyProcessingEvent() {

    OutboxEventEntity event =
        repository.saveAndFlush(
            OutboxEventEntity.pending(UUID.randomUUID(), "ExpenseSubmittedEvent", "{}"));
    int firstClaim = repository.claimForProcessing(event.getId());
    int secondClaim = repository.claimForProcessing(event.getId());
    assertThat(firstClaim).isEqualTo(1);
    assertThat(secondClaim).isZero();
  }
}