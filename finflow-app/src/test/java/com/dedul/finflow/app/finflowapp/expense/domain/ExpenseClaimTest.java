package com.dedul.finflow.app.finflowapp.expense.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.dedul.finflow.app.finflowapp.account.domain.CurrencyCode;
import com.dedul.finflow.app.finflowapp.account.domain.Money;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ExpenseClaimTest {
  @Test
  void shouldCreateDraftExpense() {
    ExpenseClaim expense =
        ExpenseClaim.createDraft(
            UUID.randomUUID(),
            Money.zero("EUR").add(new Money(new BigDecimal("10.00"), CurrencyCode.of("EUR"))),
            ExpenseCategory.TRAVEL,
            " Taxi ");

    assertThat(expense.status()).isEqualTo(ExpenseStatus.DRAFT);
    assertThat(expense.amount().amount()).isEqualByComparingTo("10.0000");
    assertThat(expense.amount().currency().value()).isEqualTo("EUR");
    assertThat(expense.description()).isEqualTo("Taxi");
    assertThat(expense.createdAt()).isNotNull();
    assertThat(expense.submittedAt()).isNull();
  }

  @Test
  void shouldSubmitDraftExpense() {
    ExpenseClaim expense = draftExpense();
    expense.submit();
    assertThat(expense.status()).isEqualTo(ExpenseStatus.SUBMITTED);
    assertThat(expense.submittedAt()).isNotNull();
  }

  @Test
  void shouldRejectSubmittingAlreadySubmittedExpense() {
    ExpenseClaim expense = draftExpense();
    expense.submit();
    assertThatThrownBy(expense::submit)
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Only DRAFT expense can be submitted");
  }

  @Test
  void shouldCancelDraftExpense() {
    ExpenseClaim expense = draftExpense();
    expense.cancel();
    assertThat(expense.status()).isEqualTo(ExpenseStatus.CANCELLED);
    assertThat(expense.cancelledAt()).isNotNull();
  }

  @Test
  void shouldRejectCancellingSubmittedExpense() {
    ExpenseClaim expense = draftExpense();
    expense.submit();
    assertThatThrownBy(expense::cancel)
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Only DRAFT expense can be cancelled");
  }

  @Test
  void shouldRejectZeroAmount() {

    assertThatThrownBy(
            () ->
                ExpenseClaim.createDraft(
                    UUID.randomUUID(), Money.zero("EUR"), ExpenseCategory.TRAVEL, "test"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Expense amount must be positive");
  }

  private ExpenseClaim draftExpense() {
    return ExpenseClaim.createDraft(
        UUID.randomUUID(),
        new Money(new BigDecimal("100.00"), CurrencyCode.of("EUR")),
        ExpenseCategory.TRAVEL,
        "Taxi");
  }
}
