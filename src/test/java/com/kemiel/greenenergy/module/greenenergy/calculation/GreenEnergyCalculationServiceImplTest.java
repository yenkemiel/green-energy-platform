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

    @Test
    @DisplayName("calculateAchievementRate：總綠電量小於用電量，回傳正常比例")
    void calculateAchievementRate_belowUsage_returnsRatio() {
        assertThat(service.calculateAchievementRate(
                new BigDecimal("500"), new BigDecimal("1000")))
                .isEqualByComparingTo(new BigDecimal("0.5000"));
    }

    @Test
    @DisplayName("calculateAchievementRate：總綠電量超過用電量，封頂回傳 1.0")
    void calculateAchievementRate_exceedsUsage_returnsCappedAtOne() {
        assertThat(service.calculateAchievementRate(
                new BigDecimal("1500"), new BigDecimal("1000")))
                .isEqualByComparingTo(BigDecimal.ONE);
    }

    @Test
    @DisplayName("calculateAchievementRate：usageKwh 為 0，回傳 0")
    void calculateAchievementRate_zeroUsage_returnsZero() {
        assertThat(service.calculateAchievementRate(
                new BigDecimal("500"), BigDecimal.ZERO))
                .isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("calculateAchievementRate：usageKwh 為 null，回傳 0")
    void calculateAchievementRate_nullUsage_returnsZero() {
        assertThat(service.calculateAchievementRate(new BigDecimal("500"), null))
                .isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("calculateGap：需要量大於已累積量，回傳正數缺口")
    void calculateGap_positiveGap_returnsDifference() {
        assertThat(service.calculateGap(
                new BigDecimal("1000"), new BigDecimal("600")))
                .isEqualByComparingTo(new BigDecimal("400"));
    }

    @Test
    @DisplayName("calculateGap：已累積量超過需要量，回傳 0")
    void calculateGap_alreadyMet_returnsZero() {
        assertThat(service.calculateGap(
                new BigDecimal("500"), new BigDecimal("800")))
                .isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("calculateSurplus：總綠電量超過用電量，回傳正數結餘")
    void calculateSurplus_exceedsUsage_returnsDifference() {
        assertThat(service.calculateSurplus(
                new BigDecimal("800"), new BigDecimal("500")))
                .isEqualByComparingTo(new BigDecimal("300"));
    }

    @Test
    @DisplayName("calculateSurplus：總綠電量小於用電量，回傳 0")
    void calculateSurplus_belowUsage_returnsZero() {
        assertThat(service.calculateSurplus(
                new BigDecimal("300"), new BigDecimal("500")))
                .isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("calculateSurplus：usageKwh 為 null，回傳 0")
    void calculateSurplus_nullUsage_returnsZero() {
        assertThat(service.calculateSurplus(new BigDecimal("800"), null))
                .isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("calculateRequiredGreenKwh：年度用電量 1,000,000 度、目標比例 0.75，應為 750,000 度")
    void calculateRequiredGreenKwh_validInput_returnsProduct() {
        assertThat(service.calculateRequiredGreenKwh(
                new BigDecimal("1000000"), new BigDecimal("0.75")))
                .isEqualByComparingTo(new BigDecimal("750000.0000"));
    }
}
