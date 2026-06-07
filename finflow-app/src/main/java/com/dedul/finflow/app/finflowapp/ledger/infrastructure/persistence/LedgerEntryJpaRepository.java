package com.dedul.finflow.app.finflowapp.ledger.infrastructure.persistence;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LedgerEntryJpaRepository extends JpaRepository<LedgerEntryEntity, UUID> {

  List<LedgerEntryEntity> findAllByTransactionId(UUID transactionId);

  List<LedgerEntryEntity> findAllByAccountIdOrderByCreatedAtDesc(UUID accountId);
}
