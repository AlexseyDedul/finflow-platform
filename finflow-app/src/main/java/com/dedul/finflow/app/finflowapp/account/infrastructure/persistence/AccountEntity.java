package com.dedul.finflow.app.finflowapp.account.infrastructure.persistence;

import com.dedul.finflow.app.finflowapp.account.domain.AccountStatus;
import com.dedul.finflow.app.finflowapp.account.domain.AccountType;
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
@Table(name = "accounts")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AccountEntity {
  @Id private UUID id;

  @Column(name = "owner_id", nullable = false)
  private UUID ownerId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 50)
  private AccountType type;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 50)
  private AccountStatus status;

  @Column(nullable = false, precision = 19, scale = 4)
  private BigDecimal balance;

  @Column(nullable = false, length = 3)
  private String currency;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;
}
