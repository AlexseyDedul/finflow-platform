package com.dedul.finflow.app.finflowapp.reporting.infrastructure.persistence;

import com.dedul.finflow.app.finflowapp.reporting.domain.ReportJobStatus;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReportJobRepository extends JpaRepository<ReportJobEntity, UUID> {
  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(
      """
    update ReportJobEntity j
    set j.status = :runningStatus,
        j.startedAt = :startedAt
    where j.id = :id
        and j.status = :pendingStatus
            """)
  int claimPendingJob(
      @Param("id") UUID id,
      @Param("runningStatus") ReportJobStatus runningStatus,
      @Param("startedAt") Instant startedAt,
      @Param("pendingStatus") ReportJobStatus pendingStatus);

  default int claimPendingJob(UUID id) {
    return claimPendingJob(id, ReportJobStatus.RUNNING, Instant.now(), ReportJobStatus.PENDING);
  }

  long countByStatus(ReportJobStatus status);
}
