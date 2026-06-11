package com.dedul.finflow.app.finflowapp.shared.outbox;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OutboxEventRepository extends JpaRepository<OutboxEventEntity, UUID> {

  List<OutboxEventEntity> findByStatusOrderByCreatedAtAsc(
      OutboxEventStatus status, Pageable pageable);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(
      """
    update OutboxEventEntity e
      set e.status = :processingStatus,
        e.processingStartedAt = :processingStartedAt
      where e.id = :id
        and e.status in :claimableStatuses
      """)
  int claimForProcessing(
      @Param("id") UUID id,
      @Param("processingStatus") OutboxEventStatus processingStatus,
      @Param("processingStartedAt") Instant processingStartedAt,
      @Param("claimableStatuses") Collection<OutboxEventStatus> claimableStatuses
  );

  default int claimForProcessing(UUID id) {
    return claimForProcessing(
        id,
        OutboxEventStatus.PROCESSING,
        Instant.now(),
        List.of(OutboxEventStatus.PENDING, OutboxEventStatus.FAILED));
  }

  long countByStatus(OutboxEventStatus status);
}
