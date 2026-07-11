package com.kemiel.greenenergy.module.greenenergy.calculation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * GreenEnergyCalculationServiceImpl 純計算方法單元測試，
 * 測試對象不觸及任何 Mapper，直接以 null 依賴建構實例
 */
@DisplayName("GreenEnergyCalculationServiceImpl 純計算方法單元測試")
class GreenEnergyCalculationServiceImplTest {

    private final GreenEnergyCalculationServiceImpl service =
            new GreenEnergyCalculationServiceImpl(null, null, null, null, null, null);

    @Test
    @DisplayName("1000 kWh 應換算為 0.49 公噸 CO₂")
    void calculateCo2Reduced_1000kwh_returns049ton() {
        assertThat(service.calculateCo2Reduced(new BigDecimal("1000")))
                .isEqualByComparingTo(new BigDecimal("0.49"));
    }

    @Test
    @DisplayName("0 kWh 應換算為 0 公噸 CO₂")
    void calculateCo2Reduced_zeroKwh_returnsZero() {
        assertThat(service.calculateCo2Reduced(BigDecimal.ZERO))
                .isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("小數 kWh 應正確四捨五入至 2 位小數")
    void calculateCo2Reduced_decimalKwh_roundsCorrectly() {
        assertThat(service.calculateCo2Reduced(new BigDecimal("123.456")))
                .isEqualByComparingTo(new BigDecimal("0.06"));
    }

    @Test
    @DisplayName("理論月發電量：10 kW 在 2026 年 7 月（31 天）應為 1085.0000 kWh")
    void calculateTheoreticalSolarKwh_july_returns1085() {
        assertThat(service.calculateTheoreticalSolarKwh(
                new BigDecimal("10"), java.time.YearMonth.of(2026, 7)))
                .isEqualByComparingTo(new BigDecimal("1085.0000"));
    }

    @Test
    @DisplayName("憑證換算：3 張 T-REC 應為 3000 kWh")
    void calculateCertificateKwh_threeCertificates_returns3000() {
        assertThat(service.calculateCertificateKwh(3))
                .isEqualByComparingTo(new BigDecimal("3000"));
    }
}
