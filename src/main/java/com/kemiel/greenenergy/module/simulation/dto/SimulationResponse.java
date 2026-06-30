package com.kemiel.greenenergy.module.simulation.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * RE100 達成模擬回應 DTO
 */
@Getter
@Builder
public class SimulationResponse {

    private BigDecimal currentGreenKwh;
    private BigDecimal currentAchievementRate;
    private BigDecimal simulatedAdditionalKwh;
    private BigDecimal simulatedTotalGreenKwh;
    private BigDecimal simulatedAchievementRate;
    private BigDecimal simulatedGapKwh;
    private SimulationBreakdownResponse breakdown;
}
