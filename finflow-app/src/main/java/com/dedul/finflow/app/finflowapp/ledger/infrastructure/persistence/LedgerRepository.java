package com.dedul.finflow.app.finflowapp.ledger.infrastructure.persistence;

import com.dedul.finflow.app.finflowapp.ledger.domain.LedgerEntry;
import com.dedul.finflow.app.finflowapp.ledger.domain.LedgerReferenceType;
import com.dedul.finflow.app.finflowapp.ledger.domain.LedgerTransaction;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LedgerRepository {

  private final LedgerTransactionJpaRepository transactionJpaRepository;
  private final LedgerEntryJpaRepository entryJpaRepository;

  public LedgerTransaction save(LedgerTransaction transaction) {
    transactionJpaRepository.save(toTransactionEntity(transaction));

    List<LedgerEntryEntity> entryEntities =
        transaction.getEntries().stream().map(this::toEntryEntity).toList();

    entryJpaRepository.saveAll(entryEntities);

    return transaction;
  }

  public boolean existsByReference(UUID referenceId, LedgerReferenceType referenceType) {
    return transactionJpaRepository.existsByReferenceIdAndReferenceType(referenceId, referenceType);
  }

  public Optional<LedgerTransaction> findByReference(
      UUID referenceId, LedgerReferenceType referenceType) {
    return transactionJpaRepository
        .findByReferenceIdAndReferenceType(referenceId, referenceType)
        .map(this::toDomain);
  }

  public List<LedgerEntry> findEntriesByAccountId(UUID accountId) {
    return entryJpaRepository.findAllByAccountIdOrderByCreatedAtDesc(accountId).stream()
        .map(this::toEntryDomain)
        .toList();
  }

  private LedgerTransaction toDomain(LedgerTransactionEntity entity) {
    List<LedgerEntry> entries =
        entryJpaRepository.findAllByTransactionId(entity.getId()).stream()
            .map(this::toEntryDomain)
            .toList();

    return LedgerTransaction.restore(
        entity.getId(),
        entity.getReferenceId(),
        entity.getReferenceType(),
        entity.getStatus(),
        entity.getCreatedAt(),
        entries);
  }

  private LedgerTransactionEntity toTransactionEntity(LedgerTransaction transaction) {
    return new LedgerTransactionEntity(
        transaction.getId(),
        transaction.getReferenceId(),
        transaction.getReferenceType(),
        transaction.getStatus(),
        transaction.getCreatedAt());
  }

  private LedgerEntryEntity toEntryEntity(LedgerEntry entry) {
    return new LedgerEntryEntity(
        entry.getId(),
        entry.getTransactionId(),
        entry.getAccountId(),
        entry.getDirection(),
        entry.getAmount(),
        entry.getCurrency(),
        entry.getCreatedAt());
  }

  private LedgerEntry toEntryDomain(LedgerEntryEntity entity) {
    return LedgerEntry.restore(
        entity.getId(),
        entity.getTransactionId(),
        entity.getAccountId(),
        entity.getDirection(),
        entity.getAmount(),
        entity.getCurrency(),
        entity.getCreatedAt());
  }
}
