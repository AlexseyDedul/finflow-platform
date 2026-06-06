package com.dedul.finflow.app.finflowapp.account.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Account {
  private final UUID id;
  private final UUID ownerId;
  private final AccountType type;
  private AccountStatus status;
  private Money balance;
  private final Instant createdAt;

  private Account(
      UUID id,
      UUID ownerId,
      AccountType type,
      AccountStatus status,
      Money balance,
      Instant createdAt) {
    this.id = Objects.requireNonNull(id);
    this.ownerId = Objects.requireNonNull(ownerId);
    this.type = Objects.requireNonNull(type);
    this.status = Objects.requireNonNull(status);
    this.balance = Objects.requireNonNull(balance);
    this.createdAt = Objects.requireNonNull(createdAt);
  }

  public static Account open(UUID ownerId, AccountType type, CurrencyCode currency) {
    return new Account(
        UUID.randomUUID(),
        ownerId,
        type,
        AccountStatus.ACTIVE,
        Money.zero(currency),
        Instant.now());
  }

  public static Account restore(
      UUID id,
      UUID ownerId,
      AccountType type,
      AccountStatus status,
      Money balance,
      Instant createdAt) {
    return new Account(id, ownerId, type, status, balance, createdAt);
  }

  public void block() {
    if (status == AccountStatus.CLOSED) {
      throw new IllegalStateException("Closed account cannot be blocked");
    }
    status = AccountStatus.BLOCKED;
  }

  public void activate() {
    if (status == AccountStatus.CLOSED) {
      throw new IllegalStateException("Closed account cannot be activated");
    }
    status = AccountStatus.ACTIVE;
  }

  public void close() {
    if (balance.amount().signum() != 0) {
      throw new IllegalStateException("Account with non-zero balance cannot be closed");
    }
    status = AccountStatus.CLOSED;
  }

  public boolean isActive() {
    return status == AccountStatus.ACTIVE;
  }

  public UUID id() {
    return id;
  }

  public UUID ownerId() {
    return ownerId;
  }

  public AccountType type() {
    return type;
  }

  public AccountStatus status() {
    return status;
  }

  public Money balance() {
    return balance;
  }

  public Instant createdAt() {
    return createdAt;
  }
}
