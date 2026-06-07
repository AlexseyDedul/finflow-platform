package com.dedul.finflow.app.finflowapp.ledger.infrastructure.persistence;

import com.dedul.finflow.app.finflowapp.ledger.domain.LedgerReferenceType;
import com.dedul.finflow.app.finflowapp.ledger.domain.LedgerTransactionStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ledger_transactions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class LedgerTransactionEntity {

  @Id private UUID id;

  @Column(name = "reference_id", nullable = false)
  private UUID referenceId;

  @Enumerated(EnumType.STRING)
  @Column(name = "reference_type", nullable = false, length = 100)
  private LedgerReferenceType referenceType;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 50)
  private LedgerTransactionStatus status;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;
}
