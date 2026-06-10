package com.dedul.finflow.app.finflowapp.expense.infrastructure.persistence;

import com.dedul.finflow.app.finflowapp.expense.domain.ExpenseStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseClaimJpaRepository extends JpaRepository<ExpenseClaimEntity, UUID> {
  List<ExpenseClaimEntity> findAllByEmployeeIdOrderByCreatedAtDesc(UUID employeeId);

  List<ExpenseClaimEntity> findAllByEmployeeIdAndStatusOrderByCreatedAtDesc(
      UUID employeeId, ExpenseStatus status);

  List<ExpenseClaimEntity> findByCreatedAtGreaterThanEqualAndCreatedAtLessThan(
      Instant from,
      Instant to
  );
}
