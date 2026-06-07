package com.dedul.finflow.app.finflowapp.ledger.infrastructure.persistence;

import com.dedul.finflow.app.finflowapp.ledger.domain.LedgerEntryDirection;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ledger_entries")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class LedgerEntryEntity {

  @Id private UUID id;

  @Column(name = "transaction_id", nullable = false)
  private UUID transactionId;

  @Column(name = "account_id", nullable = false)
  private UUID accountId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private LedgerEntryDirection direction;

  @Column(nullable = false, precision = 19, scale = 4)
  private BigDecimal amount;

  @Column(nullable = false, length = 3)
  private String currency;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;
}
