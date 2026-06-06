package com.dedul.finflow.app.finflowapp.expense.infrastructure.persistence;

import com.dedul.finflow.app.finflowapp.expense.domain.ExpenseCategory;
import com.dedul.finflow.app.finflowapp.expense.domain.ExpenseStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "expense_claims")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseClaimEntity {
  @Id private UUID id;

  @Column(name = "employee_id", nullable = false)
  private UUID employeeId;

  @Column(nullable = false, precision = 19, scale = 4)
  private BigDecimal amount;

  @Column(nullable = false, length = 3)
  private String currency;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 100)
  private ExpenseCategory category;

  @Column(length = 2000)
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 50)
  private ExpenseStatus status;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "submitted_at")
  private Instant submittedAt;

  @Column(name = "cancelled_at")
  private Instant cancelledAt;
}
