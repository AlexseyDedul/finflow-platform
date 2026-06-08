package com.dedul.finflow.app.finflowapp.ledger.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class LedgerTransactionTest {

  @Test
  void shouldPostBalancedLedgerTransaction() {
    LedgerTransaction transaction =
        LedgerTransaction.post(
            UUID.randomUUID(),
            LedgerReferenceType.EXPENSE_CLAIM,
            List.of(
                entry(LedgerEntryDirection.DEBIT, "249.99"),
                entry(LedgerEntryDirection.CREDIT, "249.99")));

    assertThat(transaction.getId()).isNotNull();
    assertThat(transaction.getStatus()).isEqualTo(LedgerTransactionStatus.POSTED);
    assertThat(transaction.getEntries()).hasSize(2);
  }

  @Test
  void shouldRejectUnbalancedLedgerTransaction() {
    assertThatThrownBy(
            () ->
                LedgerTransaction.post(
                    UUID.randomUUID(),
                    LedgerReferenceType.EXPENSE_CLAIM,
                    List.of(
                        entry(LedgerEntryDirection.DEBIT, "249.99"),
                        entry(LedgerEntryDirection.CREDIT, "100.00"))))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("ledger transaction must be balanced");
  }

  @Test
  void shouldRejectMixedCurrencies() {
    assertThatThrownBy(
            () ->
                LedgerTransaction.post(
                    UUID.randomUUID(),
                    LedgerReferenceType.EXPENSE_CLAIM,
                    List.of(
                        new LedgerTransaction.PostLedgerEntryCommand(
                            UUID.randomUUID(),
                            LedgerEntryDirection.DEBIT,
                            new BigDecimal("100.00"),
                            "EUR"),
                        new LedgerTransaction.PostLedgerEntryCommand(
                            UUID.randomUUID(),
                            LedgerEntryDirection.CREDIT,
                            new BigDecimal("100.00"),
                            "USD"))))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("all ledger entries must have the same currency");
  }

  @Test
  void shouldRejectSingleEntryTransaction() {
    assertThatThrownBy(
            () ->
                LedgerTransaction.post(
                    UUID.randomUUID(),
                    LedgerReferenceType.EXPENSE_CLAIM,
                    List.of(entry(LedgerEntryDirection.DEBIT, "100.00"))))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("ledger transaction must have at least 2 entries");
  }

  private LedgerTransaction.PostLedgerEntryCommand entry(
      LedgerEntryDirection direction, String amount) {
    return new LedgerTransaction.PostLedgerEntryCommand(
        UUID.randomUUID(), direction, new BigDecimal(amount), "EUR");
  }
}
