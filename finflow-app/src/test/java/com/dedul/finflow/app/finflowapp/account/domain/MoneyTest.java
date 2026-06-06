package com.dedul.finflow.app.finflowapp.account.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class MoneyTest {
  @Test
  void shouldNormalizeScaleAndCurrency() {
    Money money = new Money(new BigDecimal("10.5"), CurrencyCode.of("eur"));
    assertThat(money.amount()).isEqualByComparingTo("10.5000");
    assertThat(money.currency()).isEqualTo(CurrencyCode.eur());
  }

  @Test
  void shouldAddMoneyWithSameCurrency() {
    Money first = new Money(new BigDecimal("10.00"), CurrencyCode.of("EUR"));
    Money second = new Money(new BigDecimal("5.25"), CurrencyCode.of("EUR"));
    Money result = first.add(second);
    assertThat(result.amount()).isEqualByComparingTo("15.2500");
  }

  @Test
  void shouldRejectDifferentCurrencies() {
    Money eur = new Money(new BigDecimal("10.00"), CurrencyCode.of("EUR"));
    Money usd = new Money(new BigDecimal("10.00"), CurrencyCode.of("USD"));

    assertThatThrownBy(() -> eur.add(usd))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Currency mismatch");
  }
}
