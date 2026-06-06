package com.dedul.finflow.app.finflowapp.account.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public record Money(BigDecimal amount, CurrencyCode currency) {
  private static final int SCALE = 4;

  public Money {
    Objects.requireNonNull(amount, "amount must not be null");
    Objects.requireNonNull(currency, "currency must not be null");

    amount = amount.setScale(SCALE, RoundingMode.HALF_UP);
  }

  public static Money zero(CurrencyCode currency) {
    return new Money(BigDecimal.ZERO, currency);
  }

  public static Money zero(String currency) {
    return zero(CurrencyCode.of(currency));
  }

  public boolean isNegative() {
    return amount.signum() < 0;
  }

  public boolean isPositive() {
    return amount.signum() > 0;
  }

  public Money add(Money other) {
    requireSameCurrency(other);
    return new Money(amount.add(other.amount), currency);
  }

  public Money subtract(Money other) {
    requireSameCurrency(other);
    return new Money(amount.subtract(other.amount), currency);
  }

  private void requireSameCurrency(Money other) {
    if (!currency.equals(other.currency)) {
      throw new IllegalArgumentException(
          "Currency mismatch: " + currency + " != " + other.currency);
    }
  }
}
