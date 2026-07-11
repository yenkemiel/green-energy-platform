package com.kemiel.greenenergy.module.simulation.service.impl;

import com.kemiel.greenenergy.module.greenenergy.calculation.GreenEnergyCalculationService;
import com.kemiel.greenenergy.module.greenenergy.calculation.dto.MonthlySummaryResult;
import com.kemiel.greenenergy.module.greenenergy.calculation.dto.SimulationResult;
import com.kemiel.greenenergy.module.simulation.dto.SimulationBreakdownResponse;
import com.kemiel.greenenergy.module.simulation.dto.SimulationRequest;
import com.kemiel.greenenergy.module.simulation.dto.SimulationResponse;
import com.kemiel.greenenergy.module.simulation.service.SimulationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * RE100 達成模擬 Service 實作
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SimulationServiceImpl implements SimulationService {

    private final GreenEnergyCalculationService calculationService;

    /**
     * 執行 RE100 達成模擬，彙整今年 1 月至當月的實際綠電量（LOCKED 月份讀 snapshot，
     * OPEN 月份動態計算）；若尚未設定年度目標，requiredGreenKwh 與 simulatedGapKwh
     * 皆回傳 null，語意與 DashboardServiceImpl 無目標情境一致
     *
     * @param request 模擬請求（假設增加的合約供電量與採購張數）
     */
    @Override
    public SimulationResponse simulate(SimulationRequest request) {
        log.info("執行 RE100 達成模擬，request={}", request);

        int currentYear = LocalDate.now().getYear();
        int currentMonth = LocalDate.now().getMonthValue();

        BigDecimal totalGreenKwh = BigDecimal.ZERO;
        BigDecimal totalUsageKwh = BigDecimal.ZERO;

        for (int month = 1; month <= currentMonth; month++) {
            MonthlySummaryResult result =
                    calculationService.getEffectiveMonthlySummary(currentYear, month);
            totalGreenKwh = totalGreenKwh.add(result.getTotalGreenKwh());
            if (result.getUsageKwh() != null) {
                totalUsageKwh = totalUsageKwh.add(result.getUsageKwh());
            }
        }

        BigDecimal requiredGreenKwh = calculationService.resolveRequiredGreenKwh(currentYear);

        SimulationResult result = calculationService.simulate(
                totalGreenKwh,
                totalUsageKwh,
                requiredGreenKwh,
                request.getAdditionalContractKwh(),
                request.getAdditionalProcurementQuantity());

        return SimulationResponse.builder()
                .currentGreenKwh(result.getCurrentGreenKwh())
                .currentAchievementRate(result.getCurrentAchievementRate())
                .simulatedAdditionalKwh(result.getSimulatedAdditionalKwh())
                .simulatedTotalGreenKwh(result.getSimulatedTotalGreenKwh())
                .simulatedAchievementRate(result.getSimulatedAchievementRate())
                .simulatedGapKwh(result.getSimulatedGapKwh())
                .breakdown(SimulationBreakdownResponse.builder()
                        .additionalContractKwh(result.getBreakdown().getAdditionalContractKwh())
                        .additionalProcurementKwh(
                                result.getBreakdown().getAdditionalProcurementKwh())
                        .build())
                .build();
    }
}