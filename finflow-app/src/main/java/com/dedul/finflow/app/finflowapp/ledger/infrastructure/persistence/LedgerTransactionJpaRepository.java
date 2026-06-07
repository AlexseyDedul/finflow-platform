package com.dedul.finflow.app.finflowapp.ledger.infrastructure.persistence;

import com.dedul.finflow.app.finflowapp.ledger.domain.LedgerReferenceType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LedgerTransactionJpaRepository
    extends JpaRepository<LedgerTransactionEntity, UUID> {

  boolean existsByReferenceIdAndReferenceType(UUID referenceId, LedgerReferenceType referenceType);

  Optional<LedgerTransactionEntity> findByReferenceIdAndReferenceType(
      UUID referenceId, LedgerReferenceType referenceType);
}
