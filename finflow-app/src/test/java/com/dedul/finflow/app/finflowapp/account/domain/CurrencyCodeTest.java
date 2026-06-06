package com.dedul.finflow.app.finflowapp.account.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class CurrencyCodeTest {
  @Test
  void shouldNormalizeCurrencyCode() {
    CurrencyCode currency = CurrencyCode.of(" eur ");
    assertThat(currency.value()).isEqualTo("EUR");
  }

  @Test
  void shouldRejectUnknownCurrencyCode() {
    assertThatThrownBy(() -> CurrencyCode.of("AAA"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Unknown ISO-4217 currency code");
  }

  @Test
  void shouldRejectUnsupportedCurrencyCode() {
    assertThatThrownBy(() -> CurrencyCode.of("JPY"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Unsupported currency code");
  }

  @Test
  void shouldRejectInvalidLength() {
    assertThatThrownBy(() -> CurrencyCode.of("EU"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("exactly 3 characters");
  }
}
