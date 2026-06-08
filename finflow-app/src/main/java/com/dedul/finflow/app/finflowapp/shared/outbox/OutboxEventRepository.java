package com.dedul.finflow.app.finflowapp.shared.outbox;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxEventRepository extends JpaRepository<OutboxEventEntity, UUID> {

  List<OutboxEventEntity> findByStatusOrderByCreatedAtAsc(
      OutboxEventStatus status, Pageable pageable);
}
