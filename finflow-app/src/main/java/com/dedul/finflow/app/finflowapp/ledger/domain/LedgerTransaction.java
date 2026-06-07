package com.dedul.finflow.app.finflowapp.ledger.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LedgerTransaction {

  private final UUID id;
  private final UUID referenceId;
  private final LedgerReferenceType referenceType;
  private final LedgerTransactionStatus status;
  private final Instant createdAt;
  private final List<LedgerEntry> entries;

  public static LedgerTransaction post(
      UUID referenceId,
      LedgerReferenceType referenceType,
      List<PostLedgerEntryCommand> entryCommands) {
    UUID transactionId = UUID.randomUUID();

    List<LedgerEntry> entries =
        entryCommands.stream()
            .map(
                command ->
                    LedgerEntry.create(
                        transactionId,
                        command.accountId(),
                        command.direction(),
                        command.amount(),
                        command.currency()))
            .toList();

    validateTransaction(referenceId, referenceType, entries);

    return new LedgerTransaction(
        transactionId,
        referenceId,
        referenceType,
        LedgerTransactionStatus.POSTED,
        Instant.now(),
        entries);
  }

  public static LedgerTransaction restore(
      UUID id,
      UUID referenceId,
      LedgerReferenceType referenceType,
      LedgerTransactionStatus status,
      Instant createdAt,
      List<LedgerEntry> entries) {
    if (id == null) {
      throw new IllegalArgumentException("id must not be null");
    }
    if (status == null) {
      throw new IllegalArgumentException("status must not be null");
    }
    if (createdAt == null) {
      throw new IllegalArgumentException("createdAt must not be null");
    }

    validateTransaction(referenceId, referenceType, entries);

    return new LedgerTransaction(
        id, referenceId, referenceType, status, createdAt, List.copyOf(entries));
  }

  private static void validateTransaction(
      UUID referenceId, LedgerReferenceType referenceType, List<LedgerEntry> entries) {
    if (referenceId == null) {
      throw new IllegalArgumentException("referenceId must not be null");
    }
    if (referenceType == null) {
      throw new IllegalArgumentException("referenceType must not be null");
    }
    if (entries == null || entries.size() < 2) {
      throw new IllegalArgumentException("ledger transaction must have at least 2 entries");
    }

    String currency = entries.getFirst().getCurrency();

    boolean sameCurrency = entries.stream().allMatch(entry -> entry.getCurrency().equals(currency));

    if (!sameCurrency) {
      throw new IllegalArgumentException("all ledger entries must have the same currency");
    }

    BigDecimal debitSum =
        entries.stream()
            .filter(entry -> entry.getDirection() == LedgerEntryDirection.DEBIT)
            .map(LedgerEntry::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    BigDecimal creditSum =
        entries.stream()
            .filter(entry -> entry.getDirection() == LedgerEntryDirection.CREDIT)
            .map(LedgerEntry::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    if (debitSum.compareTo(creditSum) != 0) {
      throw new IllegalArgumentException("ledger transaction must be balanced");
    }
  }

  public record PostLedgerEntryCommand(
      UUID accountId, LedgerEntryDirection direction, BigDecimal amount, String currency) {}
}
