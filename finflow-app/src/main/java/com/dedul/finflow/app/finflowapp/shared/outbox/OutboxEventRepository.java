package com.dedul.finflow.app.finflowapp.shared.outbox;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface OutboxEventRepository extends JpaRepository<OutboxEventEntity, UUID> {

  List<OutboxEventEntity> findByStatusOrderByCreatedAtAsc(
      OutboxEventStatus status, Pageable pageable);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(
      """
    update OutboxEventEntity e
      set e.status = PROCESSING,
        e.processingStartedAt = CURRENT_TIMESTAMP
      where e.id = :id
        and e.status in (
          PENDING, FAILED
          )
      """)
  int claimForProcessing(UUID id);

  long countByStatus(OutboxEventStatus status);
}
