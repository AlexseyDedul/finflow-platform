package com.dedul.finflow.app.finflowapp.reporting.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.dedul.finflow.app.finflowapp.reporting.domain.ReportJobStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class ReportJobRepositoryTest {

  @Autowired private ReportJobRepository repository;

  @Test
  void claimPendingJob_shouldMovePendingJobToRunning() {
    ReportJobEntity job =
        repository.saveAndFlush(ReportJobEntity.createMonthlyExpensesJob("2026-06"));

    int claimed = repository.claimPendingJob(job.getId());

    assertThat(claimed).isEqualTo(1);

    ReportJobEntity reloaded = repository.findById(job.getId()).orElseThrow();

    assertThat(reloaded.getStatus()).isEqualTo(ReportJobStatus.RUNNING);
    assertThat(reloaded.getStartedAt()).isNotNull();
  }

  @Test
  void claimPendingJob_shouldNotClaimAlreadyRunningJob() {
    ReportJobEntity job =
        repository.saveAndFlush(ReportJobEntity.createMonthlyExpensesJob("2026-06"));

    int firstClaim = repository.claimPendingJob(job.getId());
    int secondClaim = repository.claimPendingJob(job.getId());

    assertThat(firstClaim).isEqualTo(1);
    assertThat(secondClaim).isZero();
  }
}
