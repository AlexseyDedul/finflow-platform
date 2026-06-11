package com.dedul.finflow.app.finflowapp.shared.idempotency;

import static org.assertj.core.api.Assertions.assertThat;

import com.dedul.finflow.app.finflowapp.shared.events.ProcessedEventService;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import(ProcessedEventService.class)
class ProcessedEventServiceTest {

  @Autowired private ProcessedEventService service;

  @Test
  void tryMarkProcessed_shouldReturnTrueOnlyOnce() {
    UUID eventId = UUID.randomUUID();

    boolean first = service.tryMarkProcessed(eventId, "ExpenseSubmittedEvent");
    boolean second = service.tryMarkProcessed(eventId, "ExpenseSubmittedEvent");

    assertThat(first).isTrue();
    assertThat(second).isFalse();
    assertThat(service.isProcessed(eventId)).isTrue();
  }
}
