package com.dedul.finflow.app.finflowapp.ledger.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LedgerEntry {

  private final UUID id;
  private final UUID transactionId;
  private final UUID accountId;
  private final LedgerEntryDirection direction;
  private final BigDecimal amount;
  private final String currency;
  private final Instant createdAt;

  public static LedgerEntry create(
      UUID transactionId,
      UUID accountId,
      LedgerEntryDirection direction,
      BigDecimal amount,
      String currency) {
    validate(transactionId, accountId, direction, amount, currency);

    return new LedgerEntry(
        UUID.randomUUID(),
        transactionId,
        accountId,
        direction,
        amount,
        currency.trim().toUpperCase(),
        Instant.now());
  }

  public static LedgerEntry restore(
      UUID id,
      UUID transactionId,
      UUID accountId,
      LedgerEntryDirection direction,
      BigDecimal amount,
      String currency,
      Instant createdAt) {
    if (id == null) {
      throw new IllegalArgumentException("id must not be null");
    }
    if (createdAt == null) {
      throw new IllegalArgumentException("createdAt must not be null");
    }

    validate(transactionId, accountId, direction, amount, currency);

    return new LedgerEntry(
        id, transactionId, accountId, direction, amount, currency.trim().toUpperCase(), createdAt);
  }

  private static void validate(
      UUID transactionId,
      UUID accountId,
      LedgerEntryDirection direction,
      BigDecimal amount,
      String currency) {
    if (transactionId == null) {
      throw new IllegalArgumentException("transactionId must not be null");
    }
    if (accountId == null) {
      throw new IllegalArgumentException("accountId must not be null");
    }
    if (direction == null) {
      throw new IllegalArgumentException("direction must not be null");
    }
    if (amount == null || amount.signum() <= 0) {
      throw new IllegalArgumentException("amount must be positive");
    }
    if (currency == null || currency.isBlank() || currency.trim().length() != 3) {
      throw new IllegalArgumentException("currency must have 3 letters");
    }
  }
}
