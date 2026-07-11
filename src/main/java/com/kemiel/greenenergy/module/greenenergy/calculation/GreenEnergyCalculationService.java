package com.kemiel.greenenergy.module.greenenergy.calculation;

import com.kemiel.greenenergy.module.greenenergy.calculation.dto.CompletenessResult;
import com.kemiel.greenenergy.module.greenenergy.calculation.dto.MonthlySummaryResult;
import com.kemiel.greenenergy.module.greenenergy.calculation.dto.PredictionResult;
import com.kemiel.greenenergy.module.greenenergy.calculation.dto.SimulationResult;

import java.math.BigDecimal;
import java.time.YearMonth;

/**
 * 跨模組綠電計算 Service 介面
 */
public interface GreenEnergyCalculationService {

    MonthlySummaryResult calculateMonthlySummary(int year, int month);

    CompletenessResult checkCompleteness(int year, int month);

    BigDecimal calculateAchievementRate(BigDecimal totalGreenKwh, BigDecimal usageKwh);

    BigDecimal calculateGap(BigDecimal requiredGreenKwh, BigDecimal totalGreenKwh);

    BigDecimal calculateSurplus(BigDecimal totalGreenKwh, BigDecimal usageKwh);

    BigDecimal calculateCo2Reduced(BigDecimal greenKwh);

    BigDecimal calculateRequiredGreenKwh(BigDecimal annualElectricityKwh,
                                         BigDecimal re100TargetRatio);

    void writeMonthlySummarySnapshot(int year, int month, Long lockedBy);

    PredictionResult predictYearEnd(int year);

    SimulationResult simulate(BigDecimal currentGreenKwh,
                              BigDecimal currentUsageKwh,
                              BigDecimal requiredGreenKwh,
                              BigDecimal additionalContractKwh,
                              int additionalProcurementQuantity);

    MonthlySummaryResult getEffectiveMonthlySummary(int year, int month);

    BigDecimal resolveRequiredGreenKwh(int year);

    BigDecimal calculateTheoreticalSolarKwh(BigDecimal capacityKw, YearMonth yearMonth);

    BigDecimal calculateCertificateKwh(int quantity);


}
