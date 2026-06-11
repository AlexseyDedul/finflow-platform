package com.dedul.finflow.app.finflowapp.reporting.infrastructure.persistence;

import com.dedul.finflow.app.finflowapp.reporting.domain.ReportJobStatus;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ReportJobRepository extends JpaRepository<ReportJobEntity, UUID> {
  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(
      """
    update ReportJobEntity j
    set j.status = RUNNING,
        j.startedAt = CURRENT_TIMESTAMP
    where j.id = :id
        and j.status = PENDING
            """)
  int claimPendingJob(UUID id);

  long countByStatus(ReportJobStatus status);
}
