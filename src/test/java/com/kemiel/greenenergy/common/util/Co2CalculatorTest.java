package com.kemiel.greenenergy.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Co2Calculator 單元測試，驗證綠電換算 CO₂ 公噸數的計算邏輯。
 */
@DisplayName("Co2Calculator 單元測試")
class Co2CalculatorTest {

    @Test
    @DisplayName("1000 kWh 應換算為 0.4940 公噸 CO₂")
    void calculate_1000kwh_returns0494ton() {
        assertThat(Co2Calculator.calculate(new BigDecimal("1000")))
                .isEqualByComparingTo(new BigDecimal("0.4940"));
    }

    @Test
    @DisplayName("0 kWh 應換算為 0 公噸 CO₂")
    void calculate_zeroKwh_returnsZero() {
        assertThat(Co2Calculator.calculate(BigDecimal.ZERO))
                .isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("小數 kWh 應正確四捨五入至 4 位小數")
    void calculate_decimalKwh_roundsCorrectly() {
        assertThat(Co2Calculator.calculate(new BigDecimal("123.456")))
                .isEqualByComparingTo(new BigDecimal("0.0610"));
    }
}
