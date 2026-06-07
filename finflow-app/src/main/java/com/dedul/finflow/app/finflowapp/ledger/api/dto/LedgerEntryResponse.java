package com.dedul.finflow.app.finflowapp.ledger.api.dto;

import com.dedul.finflow.app.finflowapp.ledger.domain.LedgerEntryDirection;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record LedgerEntryResponse(
    UUID id,
    UUID transactionId,
    UUID accountId,
    LedgerEntryDirection direction,
    BigDecimal amount,
    String currency,
    Instant createdAt) {}
