package com.kemiel.greenenergy.module.greenenergy.calculation.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * RE100 達成模擬計算層中間承載 DTO
 */
@Data
@Builder
public class SimulationResult {

    private BigDecimal currentGreenKwh;
    private BigDecimal currentAchievementRate;
    private BigDecimal simulatedAdditionalKwh;
    private BigDecimal simulatedTotalGreenKwh;
    private BigDecimal simulatedAchievementRate;
    private BigDecimal simulatedGapKwh;
    private SimulationBreakdown breakdown;
}
