package com.dedul.finflow.app.finflowapp.ledger.api.dto;

import com.dedul.finflow.app.finflowapp.ledger.domain.LedgerReferenceType;
import com.dedul.finflow.app.finflowapp.ledger.domain.LedgerTransactionStatus;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record LedgerTransactionResponse(
    UUID id,
    UUID referenceId,
    LedgerReferenceType referenceType,
    LedgerTransactionStatus status,
    Instant createdAt,
    List<LedgerEntryResponse> entries) {}
